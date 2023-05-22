package com.passbolt.mobile.android.core.resources

import com.passbolt.mobile.android.common.search.SearchableMatcher
import com.passbolt.mobile.android.core.resources.usecase.AddToFavouritesUseCase
import com.passbolt.mobile.android.core.resources.usecase.CreateResourceUseCase
import com.passbolt.mobile.android.core.resources.usecase.CreateStandaloneTotpResourceUseCase
import com.passbolt.mobile.android.core.resources.usecase.DeleteResourceUseCase
import com.passbolt.mobile.android.core.resources.usecase.FavouritesInteractor
import com.passbolt.mobile.android.core.resources.usecase.GetResourceTypesUseCase
import com.passbolt.mobile.android.core.resources.usecase.GetResourcesUseCase
import com.passbolt.mobile.android.core.resources.usecase.RebuildResourcePermissionsTablesUseCase
import com.passbolt.mobile.android.core.resources.usecase.RebuildResourceTablesUseCase
import com.passbolt.mobile.android.core.resources.usecase.RebuildTagsTablesUseCase
import com.passbolt.mobile.android.core.resources.usecase.RemoveFromFavouritesUseCase
import com.passbolt.mobile.android.core.resources.usecase.ResourceInteractor
import com.passbolt.mobile.android.core.resources.usecase.ResourceShareInteractor
import com.passbolt.mobile.android.core.resources.usecase.ShareResourceUseCase
import com.passbolt.mobile.android.core.resources.usecase.SimulateShareResourceUseCase
import com.passbolt.mobile.android.core.resources.usecase.UpdateResourceUseCase
import com.passbolt.mobile.android.core.resources.usecase.UpdateStandaloneTotpResourceUseCase
import org.koin.core.module.dsl.singleOf
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
val resourcesModule = module {
    singleOf(::GetResourcesUseCase)
    singleOf(::GetResourceTypesUseCase)
    singleOf(::ResourceInteractor)
    singleOf(::CreateResourceUseCase)
    singleOf(::SearchableMatcher)
    singleOf(::DeleteResourceUseCase)
    singleOf(::UpdateResourceUseCase)
    singleOf(::SecretInputCreator)
    singleOf(::RebuildResourceTablesUseCase)
    singleOf(::RebuildTagsTablesUseCase)
    singleOf(::RebuildResourcePermissionsTablesUseCase)
    singleOf(::SimulateShareResourceUseCase)
    singleOf(::ShareResourceUseCase)
    singleOf(::AddToFavouritesUseCase)
    singleOf(::RemoveFromFavouritesUseCase)
    singleOf(::FavouritesInteractor)
    singleOf(::ResourceShareInteractor)
    singleOf(::CreateStandaloneTotpResourceUseCase)
    singleOf(::UpdateStandaloneTotpResourceUseCase)
}
