package com.passbolt.mobile.android.feature.resources.details

import com.passbolt.mobile.android.core.mvp.authentication.BaseAuthenticatedPresenter
import com.passbolt.mobile.android.core.mvp.coroutinecontext.CoroutineLaunchContext
import com.passbolt.mobile.android.core.mvp.session.runAuthenticatedOperation
import com.passbolt.mobile.android.feature.resources.details.more.ResourceDetailsMenuModel
import com.passbolt.mobile.android.feature.secrets.usecase.decrypt.SecretInteractor
import com.passbolt.mobile.android.ui.ResourceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

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
class ResourceDetailsPresenter(
    private val secretInteractor: SecretInteractor,
    coroutineLaunchContext: CoroutineLaunchContext
) : BaseAuthenticatedPresenter<ResourceDetailsContract.View>(coroutineLaunchContext),
    ResourceDetailsContract.Presenter {

    override var view: ResourceDetailsContract.View? = null
    private lateinit var passwordModel: ResourceModel
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + coroutineLaunchContext.ui)
    private var isPasswordVisible = false

    override fun argsReceived(passwordModel: ResourceModel) {
        this.passwordModel = passwordModel
        view?.apply {
            displayTitle(passwordModel.name)
            displayUsername(passwordModel.username)
            displayInitialsIcon(passwordModel.name, passwordModel.initials)
            if (passwordModel.url.isNotEmpty()) {
                displayUrl(passwordModel.url)
            }
            showPasswordHidden()
            showPasswordHiddenIcon()
        }
    }

    override fun viewStopped() {
        view?.apply {
            clearPasswordInput()
            showPasswordHidden()
            showPasswordHiddenIcon()
        }
    }

    override fun usernameCopyClick() {
        view?.addToClipboard(USERNAME_LABEL, passwordModel.username)
    }

    override fun urlCopyClick() {
        view?.addToClipboard(WEBSITE_LABEL, passwordModel.url)
    }

    override fun moreClick() {
        view?.navigateToMore(ResourceDetailsMenuModel(passwordModel.name))
    }

    override fun backArrowClick() {
        view?.navigateBack()
    }

    override fun secretIconClick() {
        if (!isPasswordVisible) {
            doAfterFetchAndDecrypt { decryptedSecret ->
                view?.apply {
                    showPasswordVisibleIcon()
                    showPassword(String(decryptedSecret))
                }
            }
            isPasswordVisible = true
        } else {
            view?.apply {
                clearPasswordInput()
                showPasswordHidden()
                showPasswordHiddenIcon()
            }
            isPasswordVisible = false
        }
    }

    private fun doAfterFetchAndDecrypt(action: (ByteArray) -> Unit) {
        scope.launch {
            when (val output =
                runAuthenticatedOperation(needSessionRefreshFlow, sessionRefreshedFlow) {
                    secretInteractor.fetchAndDecrypt(
                        passwordModel.resourceId
                    )
                }
            ) {
                is SecretInteractor.Output.DecryptFailure -> view?.showDecryptionFailure()
                is SecretInteractor.Output.FetchFailure -> view?.showFetchFailure()
                is SecretInteractor.Output.Success -> {
                    action(output.decryptedSecret)
                }
            }
        }
    }

    override fun menuCopyClick() {
        doAfterFetchAndDecrypt { decryptedSecret ->
            view?.addToClipboard(SECRET_LABEL, String(decryptedSecret))
        }
    }

    override fun detach() {
        scope.coroutineContext.cancelChildren()
        super<BaseAuthenticatedPresenter>.detach()
    }

    companion object {
        private const val WEBSITE_LABEL = "Website"
        private const val USERNAME_LABEL = "Username"
        private const val SECRET_LABEL = "Secret"
    }
}
