package com.passbolt.mobile.android.service

import com.passbolt.mobile.android.core.networking.DEFAULT_HTTP_CLIENT
import com.passbolt.mobile.android.core.networking.RestService
import com.passbolt.mobile.android.core.networking.RetrofitRestService
import com.passbolt.mobile.android.service.auth.AuthDataSource
import com.passbolt.mobile.android.service.auth.AuthRepository
import com.passbolt.mobile.android.service.auth.data.AuthApi
import com.passbolt.mobile.android.service.auth.data.AuthRemoteDataSource
import com.passbolt.mobile.android.service.registration.RegistrationDataSource
import com.passbolt.mobile.android.service.registration.RegistrationRepository
import com.passbolt.mobile.android.service.registration.data.RegistrationApi
import com.passbolt.mobile.android.service.registration.data.RegistrationRemoteDataSource
import com.passbolt.mobile.android.service.resource.ResourceApi
import com.passbolt.mobile.android.service.resource.ResourceDataSource
import com.passbolt.mobile.android.service.resource.ResourceRemoteDataSource
import com.passbolt.mobile.android.service.resource.ResourceRepository
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.converter.gson.GsonConverterFactory

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
val passboltApiModule = module {
    single { provideRestService(get(named(DEFAULT_HTTP_CLIENT))) }
    single { getRegistrationApi(get()) }
    single { getAuthApi(get()) }
    single { getResourceApi(get()) }

    single<RegistrationDataSource> {
        RegistrationRemoteDataSource(
            registrationApi = get()
        )
    }
    single<AuthDataSource> {
        AuthRemoteDataSource(
            authApi = get()
        )
    }
    single<ResourceDataSource> {
        ResourceRemoteDataSource(
            resourceApi = get()
        )
    }
    single {
        RegistrationRepository(
            registrationDataSource = get(),
            responseHandler = get()
        )
    }
    single {
        AuthRepository(
            authDataSource = get(),
            responseHandler = get()
        )
    }
    single {
        ResourceRepository(
            resourceDataSource = get(),
            responseHandler = get()
        )
    }
}

private fun provideRestService(okHttpClient: OkHttpClient): RestService {
    return RetrofitRestService(
        client = okHttpClient,
        converterFactory = GsonConverterFactory.create()
    )
}

private fun getRegistrationApi(restService: RestService): RegistrationApi =
    restService.service(RegistrationApi::class.java)

private fun getAuthApi(restService: RestService): AuthApi =
    restService.service(AuthApi::class.java)

private fun getResourceApi(restService: RestService): ResourceApi =
    restService.service(ResourceApi::class.java)
