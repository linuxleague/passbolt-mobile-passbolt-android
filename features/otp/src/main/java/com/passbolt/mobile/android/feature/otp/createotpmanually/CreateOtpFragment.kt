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

package com.passbolt.mobile.android.feature.otp.createotpmanually

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.passbolt.mobile.android.common.extension.setDebouncingOnClick
import com.passbolt.mobile.android.core.extension.initDefaultToolbar
import com.passbolt.mobile.android.core.mvp.scoped.BindingScopedFragment
import com.passbolt.mobile.android.feature.otp.databinding.FragmentCreateOtpBinding
import org.koin.android.ext.android.inject

class CreateOtpFragment :
    BindingScopedFragment<FragmentCreateOtpBinding>(FragmentCreateOtpBinding::inflate),
    CreateOtpContract.View {

    private val presenter: CreateOtpContract.Presenter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initDefaultToolbar(binding.toolbar)
        presenter.attach(this)
    }

    private fun setListeners() {
        with(binding) {
            advancedSettings.setDebouncingOnClick {
                presenter.advancedSettingsClick()
            }
        }
    }

    override fun navigateToCreateOtpAdvancedSettings() {
        findNavController().navigate(
            CreateOtpFragmentDirections.actionCreateOtpManuallyFragmentToCreateOtpAdvancedSettingsFragment()
        )
    }
}
