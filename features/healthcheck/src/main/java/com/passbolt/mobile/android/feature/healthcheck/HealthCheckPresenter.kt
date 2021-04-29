package com.passbolt.mobile.android.feature.healthcheck

import com.passbolt.mobile.android.core.mvp.CoroutineLaunchContext
import com.passbolt.mobile.android.core.mvp.NetworkResult
import com.passbolt.mobile.android.feature.healthcheck.usecase.GetHealthCheckUseCase
import com.passbolt.mobile.android.storage.usecase.GetPrivateKeyUseCase
import com.passbolt.mobile.android.storage.usecase.SavePrivateKeyUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

class HealthCheckPresenter @Inject constructor(
    private val getHealthCheckUseCase: GetHealthCheckUseCase,
    coroutineLaunchContext: CoroutineLaunchContext,
    private val savePrivateKeyUseCase: SavePrivateKeyUseCase,
    private val getPrivateKeyUseCase: GetPrivateKeyUseCase
) : HealthCheckContract.Presenter {

    override var view: HealthCheckContract.View? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + coroutineLaunchContext.ui)

    override fun attach(view: HealthCheckContract.View) {
        super.attach(view)
        getSystemHealth()
    }

    private fun getSystemHealth() {
        scope.launch {
            when (val response = withContext(Dispatchers.IO) { getHealthCheckUseCase.getHealthCheck() }) {
                is NetworkResult.Failure.NetworkError -> view?.showMessage("Network error")
                is NetworkResult.Failure.ServerError -> view?.showMessage("Server error")
                is NetworkResult.Success -> view?.showMessage(response.value)
            }
        }
    }

    override fun saveKey(userId: String, privateKeyCharArray: CharArray) {
        savePrivateKeyUseCase.execute(SavePrivateKeyUseCase.Input(userId, privateKeyCharArray))
    }

    override fun decryptKey(userId: String) {
        val privateKey = getPrivateKeyUseCase.execute(GetPrivateKeyUseCase.Input(userId)).privateKey
        privateKey?.let {
            view?.displayPrivateKey(it)
        }
    }
}
