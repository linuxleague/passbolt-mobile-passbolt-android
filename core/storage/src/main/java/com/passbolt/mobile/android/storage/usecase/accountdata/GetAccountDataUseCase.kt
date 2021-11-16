package com.passbolt.mobile.android.storage.usecase.accountdata

import com.passbolt.mobile.android.common.usecase.UseCase
import com.passbolt.mobile.android.storage.encrypted.EncryptedSharedPreferencesFactory
import com.passbolt.mobile.android.storage.paths.AccountDataFileName
import com.passbolt.mobile.android.storage.usecase.AVATAR_URL_KEY
import com.passbolt.mobile.android.storage.usecase.EMAIL_KEY
import com.passbolt.mobile.android.storage.usecase.SERVER_ID_KEY
import com.passbolt.mobile.android.storage.usecase.URL_KEY
import com.passbolt.mobile.android.storage.usecase.USER_FIRST_NAME_KEY
import com.passbolt.mobile.android.storage.usecase.USER_LAST_NAME_KEY
import com.passbolt.mobile.android.storage.usecase.input.UserIdInput

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

class GetAccountDataUseCase(
    private val encryptedSharedPreferencesFactory: EncryptedSharedPreferencesFactory
) : UseCase<UserIdInput, GetAccountDataUseCase.Output> {

    override fun execute(input: UserIdInput): Output {
        val fileName = AccountDataFileName(input.userId).name
        val sharedPreferences = encryptedSharedPreferencesFactory.get("$fileName.xml")

        return Output(
            sharedPreferences.getString(USER_FIRST_NAME_KEY, null),
            sharedPreferences.getString(USER_LAST_NAME_KEY, null),
            sharedPreferences.getString(EMAIL_KEY, null),
            sharedPreferences.getString(AVATAR_URL_KEY, null),
            sharedPreferences.getString(URL_KEY, "").orEmpty(),
            sharedPreferences.getString(SERVER_ID_KEY, "")
        )
    }

    class Output(
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val avatarUrl: String?,
        val url: String,
        val serverId: String?
    )
}
