package com.passbolt.mobile.android.feature.setup.scanqr

import com.passbolt.mobile.android.common.HttpsVerifier
import com.passbolt.mobile.android.common.UuidProvider
import com.passbolt.mobile.android.core.mvp.coroutinecontext.CoroutineLaunchContext
import com.passbolt.mobile.android.feature.setup.scanqr.qrparser.ParseResult
import com.passbolt.mobile.android.feature.setup.scanqr.qrparser.ScanQrParser
import com.passbolt.mobile.android.feature.setup.scanqr.usecase.UpdateTransferUseCase
import com.passbolt.mobile.android.feature.setup.summary.ResultStatus
import com.passbolt.mobile.android.storage.usecase.accountdata.SaveAccountDataUseCase
import com.passbolt.mobile.android.storage.usecase.accountdata.UpdateAccountDataUseCase
import com.passbolt.mobile.android.storage.usecase.accounts.CheckAccountExistsUseCase
import com.passbolt.mobile.android.storage.usecase.privatekey.SavePrivateKeyUseCase
import com.passbolt.mobile.android.ui.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * Passbolt - Open source password manager for teams
 * Copyright (c) 2021 Passbolt SA
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License (AGPL) as published by the Free Software Foundation version 3.
 *
 * The name "Passbolt" is a registered trademark of Passbolt SA, and Passbolt SA hereby declines to grant a trademark
 * license to "Passbolt" pursuant to the GNU Affero General Public License version 3 Section 7(e), without a separate
 * agreement with Passbolt SA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see GNU Affero General Public License v3 (http://www.gnu.org/licenses/agpl-3.0.html).
 *
 * @copyright Copyright (c) Passbolt SA (https://www.passbolt.com)
 * @license https://opensource.org/licenses/AGPL-3.0 AGPL License
 * @link https://www.passbolt.com Passbolt (tm)
 * @since v1.0
 */
class ScanQrPresenter(
    coroutineLaunchContext: CoroutineLaunchContext,
    private val updateTransferUseCase: UpdateTransferUseCase,
    private val qrParser: ScanQrParser,
    private val saveAccountDataUseCase: SaveAccountDataUseCase,
    private val uuidProvider: UuidProvider,
    private val savePrivateKeyUseCase: SavePrivateKeyUseCase,
    private val updateAccountDataUseCase: UpdateAccountDataUseCase,
    private val checkAccountExistsUseCase: CheckAccountExistsUseCase,
    private val httpsVerifier: HttpsVerifier
) : ScanQrContract.Presenter {

    override var view: ScanQrContract.View? = null

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + coroutineLaunchContext.ui)

    private lateinit var authToken: String
    private lateinit var transferUuid: String
    private lateinit var userId: String
    private var totalPages: Int by Delegates.notNull()
    private var currentPage = 0

    override fun attach(view: ScanQrContract.View) {
        super.attach(view)
        scope.launch {
            launch { qrParser.startParsing(view.scanResultChannel()) }
            launch {
                qrParser.parseResultFlow
                    .collect { processParseResult(it) }
            }
        }
        view.startAnalysis()
    }

    private suspend fun processParseResult(parserResult: ParseResult) {
        when (parserResult) {
            is ParseResult.Failure -> parserFailure(parserResult.exception)
            is ParseResult.PassboltQr -> when (parserResult) {
                is ParseResult.PassboltQr.FirstPage -> parserFirstPage(parserResult)
                is ParseResult.PassboltQr.SubsequentPage -> parserSubsequentPage(parserResult)
            }
            is ParseResult.FinishedWithSuccess -> parserFinishedWithSuccess(parserResult.armoredKey)
            is ParseResult.UserResolvableError -> when (parserResult.errorType) {
                ParseResult.UserResolvableError.ErrorType.MULTIPLE_BARCODES -> view?.showMultipleCodesInRange()
                ParseResult.UserResolvableError.ErrorType.NO_BARCODES_IN_RANGE -> view?.showCenterCameraOnBarcode()
                ParseResult.UserResolvableError.ErrorType.NOT_A_PASSBOLT_QR -> view?.showNotAPassboltQr()
            }
        }
    }

    private suspend fun parserFailure(exception: Throwable?) {
        exception?.let { Timber.e(it) }
        updateTransfer(pageNumber = currentPage, Status.ERROR)
    }

    private suspend fun parserFirstPage(firstPage: ParseResult.PassboltQr.FirstPage) {
        val userId = firstPage.content.userId
        transferUuid = firstPage.content.transferId
        authToken = firstPage.content.authenticationToken
        totalPages = firstPage.content.totalPages

        val userExistsResult = checkAccountExistsUseCase.execute(CheckAccountExistsUseCase.Input(userId))
        if (userExistsResult.exist) {
            currentPage = totalPages - 1
            updateTransferAlreadyLinked(currentPage, requireNotNull(userExistsResult.userId))
        } else if (!httpsVerifier.isHttps(firstPage.content.domain)) {
            parserFailure(Throwable("Domain should start with https"))
        } else {
            if (currentPage > 0) {
                parserFailure(Throwable("Other qr code scanning has been already started"))
            } else {
                view?.initializeProgress(totalPages)
                view?.showKeepGoing()
                saveAccountDetails(userId, firstPage.content.domain)
                updateTransfer(pageNumber = firstPage.reservedBytesDto.page + 1)
            }
        }
    }

    private suspend fun parserSubsequentPage(subsequentPage: ParseResult.PassboltQr.SubsequentPage) {
        currentPage = subsequentPage.reservedBytesDto.page
        view?.showKeepGoing()

        if (subsequentPage.reservedBytesDto.page < totalPages - 1) {
            updateTransfer(pageNumber = currentPage + 1)
        } else {
            qrParser.verifyScannedKey()
        }
    }

    private suspend fun parserFinishedWithSuccess(armoredKey: String) {
        when (savePrivateKeyUseCase.execute(SavePrivateKeyUseCase.Input(userId, armoredKey))) {
            SavePrivateKeyUseCase.Output.Failure -> {
                updateTransfer(pageNumber = currentPage, Status.ERROR)
                view?.navigateToSummary(ResultStatus.Failure(""))
            }
            SavePrivateKeyUseCase.Output.Success -> {
                updateTransfer(pageNumber = currentPage, Status.COMPLETE)
                view?.navigateToSummary(ResultStatus.Success)
            }
        }
    }

    private suspend fun updateTransferAlreadyLinked(pageNumber: Int, userId: String) {
        updateTransferUseCase.execute(
            UpdateTransferUseCase.Input(
                uuid = transferUuid,
                authToken = authToken,
                currentPage = pageNumber,
                status = Status.COMPLETE
            )
        )
        // ignoring result
        view?.navigateToSummary(ResultStatus.AlreadyLinked(userId))
    }

    private suspend fun updateTransfer(pageNumber: Int, status: Status = Status.IN_PROGRESS) {
        // in case of the first qr code is not a correct one
        if (!::transferUuid.isInitialized || !::authToken.isInitialized) {
            view?.navigateToSummary(ResultStatus.Failure(""))
            return
        }
        val response = updateTransferUseCase.execute(
            UpdateTransferUseCase.Input(
                uuid = transferUuid,
                authToken = authToken,
                currentPage = pageNumber,
                status = status
            )
        )
        when (response) {
            is UpdateTransferUseCase.Output.Failure -> {
                Timber.e(response.exception, "There was an error during transfer update")
                if (status == Status.ERROR || status == Status.CANCEL) {
                    // ignoring
                } else {
                    view?.showSomethingWentWrong()
                }
            }
            is UpdateTransferUseCase.Output.Success -> {
                onUpdateTransferSuccess(pageNumber, status, response)
            }
        }
    }

    private fun onUpdateTransferSuccess(
        pageNumber: Int,
        status: Status,
        response: UpdateTransferUseCase.Output.Success
    ) {
        view?.setProgress(pageNumber)
        when (status) {
            Status.COMPLETE -> {
                updateAccountDataUseCase.execute(
                    UpdateAccountDataUseCase.Input(
                        firstName = response.updateTransferResponseModel.firstName,
                        lastName = response.updateTransferResponseModel.lastName,
                        avatarUrl = response.updateTransferResponseModel.avatarUrl,
                        email = response.updateTransferResponseModel.email
                    )
                )
            }
            Status.ERROR -> {
                view?.navigateToSummary(ResultStatus.Failure(""))
            }
            else -> {
                // ignoring
            }
        }
    }

    private fun saveAccountDetails(serverId: String, url: String) {
        userId = uuidProvider.get()
        saveAccountDataUseCase.execute(
            SaveAccountDataUseCase.Input(
                userId = userId,
                url = url,
                serverId = serverId
            )
        )
    }

    override fun startCameraError(exc: Exception) {
        Timber.e(exc)
        view?.showStartCameraError()
    }

    override fun backClick() {
        view?.showExitConfirmation()
    }

    override fun exitConfirmClick() {
        view?.navigateBack()
    }

    override fun infoIconClick() {
        view?.showInformationDialog()
    }

    override fun detach() {
        scope.coroutineContext.cancelChildren()
        super.detach()
    }
}
