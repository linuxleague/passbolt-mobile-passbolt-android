package com.passbolt.mobile.android.core.networking.interceptor

import com.passbolt.mobile.android.core.networking.PLACEHOLDER_BASE_URL
import com.passbolt.mobile.android.storage.usecase.selectedaccount.GetCurrentApiUrlUseCase
import okhttp3.Interceptor
import okhttp3.Response

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
class ChangeableBaseUrlInterceptor(
    private val getCurrentApiUrlUseCase: GetCurrentApiUrlUseCase
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        val oldUrl = oldRequest.url
        val baseUrl: String = getCurrentApiUrlUseCase.execute(Unit).currentUrl
        val newUrl = oldUrl.toString().replaceBaseUrlWithNew(baseUrl)
        val newRequest = oldRequest.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}

private fun String.replaceBaseUrlWithNew(newUrl: String) =
    toString().replace(PLACEHOLDER_BASE_URL, newUrl)
