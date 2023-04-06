package com.passbolt.mobile.android.database.impl.resources

import com.passbolt.mobile.android.common.usecase.AsyncUseCase
import com.passbolt.mobile.android.database.DatabaseProvider
import com.passbolt.mobile.android.entity.resource.ResourceDatabaseView
import com.passbolt.mobile.android.feature.home.screen.model.HomeDisplayViewModel
import com.passbolt.mobile.android.mappers.HomeDisplayViewMapper
import com.passbolt.mobile.android.mappers.ResourceModelMapper
import com.passbolt.mobile.android.storage.usecase.selectedaccount.GetSelectedAccountUseCase
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
class GetLocalResourcesUseCase(
    private val databaseProvider: DatabaseProvider,
    private val resourceModelMapper: ResourceModelMapper,
    private val getSelectedAccountUseCase: GetSelectedAccountUseCase,
    private val homeDisplayViewMapper: HomeDisplayViewMapper
) : AsyncUseCase<GetLocalResourcesUseCase.Input, GetLocalResourcesUseCase.Output> {

    override suspend fun execute(input: Input): Output {
        val userId = requireNotNull(getSelectedAccountUseCase.execute(Unit).selectedAccount)
        val resources = databaseProvider
            .get(userId)
            .resourcesDao()
            .let {
                when (val viewType = homeDisplayViewMapper.map(input.homeDisplayView)) {
                    is ResourceDatabaseView.ByModifiedDateDescending -> it.getAllOrderedByModifiedDate(input.slugs)
                    is ResourceDatabaseView.ByNameAscending -> it.getAllOrderedByName(input.slugs)
                    is ResourceDatabaseView.IsFavourite -> it.getFavourites(input.slugs)
                    is ResourceDatabaseView.HasPermissions -> it.getWithPermissions(viewType.permissions, input.slugs)
                }
            }

        return Output(resources.map { resourceModelMapper.map(it) })
    }

    data class Input(
        val slugs: List<String>,
        val homeDisplayView: HomeDisplayViewModel = HomeDisplayViewModel.AllItems
    )

    data class Output(val resources: List<ResourceModel>)
}
