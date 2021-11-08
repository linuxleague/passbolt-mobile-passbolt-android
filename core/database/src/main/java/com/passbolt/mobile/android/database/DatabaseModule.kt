package com.passbolt.mobile.android.database

import com.passbolt.mobile.android.database.usecase.AddLocalResourceTypesUseCase
import com.passbolt.mobile.android.database.usecase.AddLocalResourceUseCase
import com.passbolt.mobile.android.database.usecase.AddLocalResourcesUseCase
import com.passbolt.mobile.android.database.usecase.GetLocalResourcesUseCase
import com.passbolt.mobile.android.database.usecase.GetResourceTypeWithFieldsUseCase
import com.passbolt.mobile.android.database.usecase.GetResourcesDatabasePassphraseUseCase
import com.passbolt.mobile.android.database.usecase.RemoveLocalResourcesUseCase
import com.passbolt.mobile.android.database.usecase.SaveResourcesDatabasePassphraseUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

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
val databaseModule = module {
    single {
        DatabaseProvider(
            getResourcesDatabasePassphraseUseCase = get(),
            androidApplication()
        )
    }
    single {
        GetResourcesDatabasePassphraseUseCase(
            encryptedSharedPreferencesFactory = get(),
            getSelectedAccountUseCase = get()
        )
    }
    single {
        SaveResourcesDatabasePassphraseUseCase(
            encryptedSharedPreferencesFactory = get(),
            getSelectedAccountUseCase = get()
        )
    }
    single {
        AddLocalResourcesUseCase(
            databaseProvider = get(),
            resourceModelMapper = get()
        )
    }
    single {
        AddLocalResourceUseCase(
            databaseProvider = get(),
            resourceModelMapper = get(),
            getSelectedAccountUseCase = get()
        )
    }
    single {
        GetLocalResourcesUseCase(
            databaseProvider = get(),
            resourceModelMapper = get()
        )
    }
    single {
        GetResourceTypeWithFieldsUseCase(
            databaseProvider = get()
        )
    }
    single {
        RemoveLocalResourcesUseCase(
            databaseProvider = get()
        )
    }
    single {
        AddLocalResourceTypesUseCase(
            databaseProvider = get(),
            resourceTypesModelMapper = get(),
            getSelectedAccountUseCase = get()
        )
    }
}
