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

package com.passbolt.mobile.android.serializers.resourcelistdeserializer

import com.passbolt.mobile.android.dto.response.PermissionDto
import com.passbolt.mobile.android.dto.response.ResourceResponseDto
import java.util.UUID

internal fun resourceWithNameOfLength(nameLength: Int, testedResourceTypeUuid: UUID) {
    ResourceResponseDto(
        id = UUID.randomUUID(),
        resourceTypeId = testedResourceTypeUuid,
        description = null,
        resourceFolderId = null,
        name = (0..(nameLength)).joinToString { "a" },
        uri = null,
        username = null,
        permission = PermissionDto(UUID.randomUUID(), 1, "", UUID.randomUUID(), "", UUID.randomUUID(), "", ""),
        favorite = null,
        modified = "",
        tags = emptyList(),
        permissions = emptyList()
    )
}

internal fun resourceWithUriOfLength(uriLength: Int, testedResourceTypeUuid: UUID) {
    ResourceResponseDto(
        id = UUID.randomUUID(),
        resourceTypeId = testedResourceTypeUuid,
        description = null,
        resourceFolderId = null,
        name = "",
        uri = (0..(uriLength)).joinToString { "a" },
        username = null,
        permission = PermissionDto(UUID.randomUUID(), 1, "", UUID.randomUUID(), "", UUID.randomUUID(), "", ""),
        favorite = null,
        modified = "",
        tags = emptyList(),
        permissions = emptyList()
    )
}

internal fun resourceWithDescriptionOfLength(descriptionLength: Int, testedResourceTypeUuid: UUID) {
    ResourceResponseDto(
        id = UUID.randomUUID(),
        resourceTypeId = testedResourceTypeUuid,
        description = (0..(descriptionLength)).joinToString { "a" },
        resourceFolderId = null,
        name = "",
        uri = null,
        username = null,
        permission = PermissionDto(UUID.randomUUID(), 1, "", UUID.randomUUID(), "", UUID.randomUUID(), "", ""),
        favorite = null,
        modified = "",
        tags = emptyList(),
        permissions = emptyList()
    )
}

internal fun resourceWithUsernameOfLength(usernameLength: Int, testedResourceTypeUuid: UUID) {
    ResourceResponseDto(
        id = UUID.randomUUID(),
        resourceTypeId = testedResourceTypeUuid,
        description = null,
        resourceFolderId = null,
        name = "",
        uri = null,
        username = (0..(usernameLength)).joinToString { "a" },
        permission = PermissionDto(UUID.randomUUID(), 1, "", UUID.randomUUID(), "", UUID.randomUUID(), "", ""),
        favorite = null,
        modified = "",
        tags = emptyList(),
        permissions = emptyList()
    )
}
