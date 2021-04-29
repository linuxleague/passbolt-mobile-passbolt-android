package com.passbolt.mobile.android.feature.healthcheck

import android.os.Bundle
import android.view.View
import com.passbolt.mobile.android.core.mvp.viewbinding.BindingFragment
import com.passbolt.mobile.android.feature.healthcheck.databinding.FragmentHealthCheckBinding
import dagger.hilt.android.AndroidEntryPoint
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

private typealias Binding = FragmentHealthCheckBinding

@AndroidEntryPoint
class HealthCheckFragment : BindingFragment<Binding>(Binding::inflate),
    HealthCheckContract.View {

    @Inject
    lateinit var presenter: HealthCheckContract.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        binding.encryptButton.setOnClickListener {
            val privateKeyCharArray = CharArray(binding.privateKeyEditText.text.length)
            binding.privateKeyEditText.text.getChars(0, binding.privateKeyEditText.text.length, privateKeyCharArray, 0)
            presenter.saveKey(binding.userIdEditText.text.toString(), privateKeyCharArray)
        }
        binding.decryptButton.setOnClickListener {
            presenter.decryptKey(binding.userIdEditText.text.toString())
        }
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    override fun showMessage(status: String) {
        binding.textviewFirst.text = status
    }

    override fun displayPrivateKey(privateKey: CharArray) {
        binding.privateKeyEditText.setText(privateKey, 0, privateKey.size)
    }
}
