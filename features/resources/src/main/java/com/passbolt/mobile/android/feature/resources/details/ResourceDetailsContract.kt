package com.passbolt.mobile.android.feature.resources.details

import com.passbolt.mobile.android.core.mvp.authentication.BaseAuthenticatedContract
import com.passbolt.mobile.android.feature.resources.details.more.ResourceDetailsMenuModel
import com.passbolt.mobile.android.ui.ResourceModel

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
interface ResourceDetailsContract {

    interface View : BaseAuthenticatedContract.View {
        fun displayTitle(title: String)
        fun displayUsername(username: String)
        fun addToClipboard(label: String, value: String)
        fun displayUrl(url: String)
        fun displayInitialsIcon(name: String, initials: String)
        fun navigateToMore(model: ResourceDetailsMenuModel)
        fun navigateBack()
        fun showProgress()
        fun showPasswordVisibleIcon()
        fun hideProgress()
        fun showDecryptionFailure()
        fun showFetchFailure()
        fun showPasswordHiddenIcon()
        fun showPasswordHidden()
        fun showPassword(decryptedSecret: String)
        fun clearPasswordInput()
        fun showDescriptionIsEncrypted()
        fun showDescription(description: String, useSecretFont: Boolean)
        fun hidePasswordEyeIcon()
    }

    interface Presenter : BaseAuthenticatedContract.Presenter<View> {
        fun argsReceived(resourceModel: ResourceModel)
        fun usernameCopyClick()
        fun urlCopyClick()
        fun moreClick()
        fun backArrowClick()
        fun secretIconClick()
        fun viewStopped()
        fun menuCopyPasswordClick()
        fun seeDescriptionButtonClick()
        fun menuCopyDescriptionClick()
    }
}
