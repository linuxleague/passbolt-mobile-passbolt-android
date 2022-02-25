package com.passbolt.mobile.android.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.passbolt.mobile.android.database.dao.ResourcesDao
import com.passbolt.mobile.android.entity.resource.Permission
import com.passbolt.mobile.android.entity.resource.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

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

class SortByModifiedDateTest {

    private lateinit var resourcesDao: ResourcesDao
    private lateinit var db: ResourceDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ResourceDatabase::class.java
        ).build()
        resourcesDao = db.resourcesDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testSortingWithNoTimeZones() = runBlocking {
        resourcesDao.insert(listOf(RESOURCE_1, RESOURCE_2, RESOURCE_3))

        val sortedByModifiedDate = resourcesDao.getAllOrderedByModifiedDate()

        assertThat(
            sortedByModifiedDate.map { it.modified.toInstant().toEpochMilli() }
        )
            .isInOrder(compareByDescending<Long> { it })
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testSortingWithTimeZones() = runBlocking {
        resourcesDao.insert(listOf(RESOURCE_1, RESOURCE_2, RESOURCE_3, RESOURCE_1_ZONE_MINUS, RESOURCE_1_ZONE_PLUS))

        val sortedByModifiedDate = resourcesDao.getAllOrderedByModifiedDate()

        assertThat(
            sortedByModifiedDate.map { it.modified.toInstant().toEpochMilli() }
        )
            .isInOrder(compareByDescending<Long> { it })
    }

    private companion object {
        private val RESOURCE_1 = Resource(
            "1", "folderid", "", Permission.READ, null, null, null, "1", false,
            ZonedDateTime.now()
        )
        private val RESOURCE_2 = Resource(
            "2", "folderid", "", Permission.READ, null, null, null, "1", false,
            ZonedDateTime.now().plusDays(1)
        )
        private val RESOURCE_3 = Resource(
            "3", "folderid", "", Permission.READ, null, null, null, "1", false,
            ZonedDateTime.now().plusDays(2)
        )
        private val RESOURCE_1_ZONE_MINUS = Resource(
            "4", "folderid", "", Permission.READ, null, null, null, "1", false,
            LocalDateTime.now().atZone(ZoneOffset.of("-08:00"))
        )
        private val RESOURCE_1_ZONE_PLUS = Resource(
            "5", "folderid", "", Permission.READ, null, null, null, "1", false,
            LocalDateTime.now().atZone(ZoneOffset.of("+08:00"))
        )
    }
}
