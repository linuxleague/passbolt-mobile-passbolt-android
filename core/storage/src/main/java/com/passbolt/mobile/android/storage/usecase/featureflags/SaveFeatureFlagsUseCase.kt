package com.passbolt.mobile.android.storage.usecase.featureflags

import com.passbolt.mobile.android.common.usecase.AsyncUseCase
import com.passbolt.mobile.android.entity.featureflags.FeatureFlagsModel
import com.passbolt.mobile.android.storage.encrypted.EncryptedSharedPreferencesFactory
import com.passbolt.mobile.android.storage.paths.FeatureFlagsFileName
import com.passbolt.mobile.android.storage.usecase.SelectedAccountUseCase
import com.passbolt.mobile.android.storage.usecase.featureflags.Constants.FOLDERS_KEY
import com.passbolt.mobile.android.storage.usecase.featureflags.Constants.PREVIEW_PASSWORD_KEY
import com.passbolt.mobile.android.storage.usecase.featureflags.Constants.PRIVACY_POLICY_KEY
import com.passbolt.mobile.android.storage.usecase.featureflags.Constants.TAGS_KEY
import com.passbolt.mobile.android.storage.usecase.featureflags.Constants.TERMS_AND_CONDITIONS_KEY
import com.passbolt.mobile.android.storage.usecase.featureflags.Constants.TOTP_KEY

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
class SaveFeatureFlagsUseCase(
    private val encryptedSharedPreferencesFactory: EncryptedSharedPreferencesFactory
) : AsyncUseCase<SaveFeatureFlagsUseCase.Input, Unit>, SelectedAccountUseCase {

    override suspend fun execute(input: Input) {
        val fileName = FeatureFlagsFileName(selectedAccountId).name
        val sharedPreferences = encryptedSharedPreferencesFactory.get("$fileName.xml")
        with(sharedPreferences.edit()) {
            putString(PRIVACY_POLICY_KEY, input.featureFlags.privacyPolicyUrl)
            putString(TERMS_AND_CONDITIONS_KEY, input.featureFlags.termsAndConditionsUrl)
            putBoolean(PREVIEW_PASSWORD_KEY, input.featureFlags.isPreviewPasswordAvailable)
            putBoolean(FOLDERS_KEY, input.featureFlags.areFoldersAvailable)
            putBoolean(TAGS_KEY, input.featureFlags.areTagsAvailable)
            putBoolean(TOTP_KEY, input.featureFlags.isTotpAvailable)
            apply()
        }
    }

    data class Input(val featureFlags: FeatureFlagsModel)
}
