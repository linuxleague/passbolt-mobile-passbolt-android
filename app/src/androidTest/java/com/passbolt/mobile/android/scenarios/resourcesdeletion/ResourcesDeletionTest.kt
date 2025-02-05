package com.passbolt.mobile.android.scenarios.resourcesdeletion

import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.passbolt.mobile.android.core.idlingresource.CreateResourceIdlingResource
import com.passbolt.mobile.android.core.idlingresource.DeleteResourceIdlingResource
import com.passbolt.mobile.android.core.idlingresource.ResourcesFullRefreshIdlingResource
import com.passbolt.mobile.android.core.idlingresource.SignInIdlingResource
import com.passbolt.mobile.android.core.navigation.ActivityIntents
import com.passbolt.mobile.android.core.navigation.AppContext
import com.passbolt.mobile.android.feature.authentication.AuthenticationMainActivity
import com.passbolt.mobile.android.feature.setup.R
import com.passbolt.mobile.android.first
import com.passbolt.mobile.android.hasDrawable
import com.passbolt.mobile.android.instrumentationTestsModule
import com.passbolt.mobile.android.intents.ManagedAccountIntentCreator
import com.passbolt.mobile.android.rules.IdlingResourceRule
import com.passbolt.mobile.android.rules.lazyActivitySetupScenarioRule
import com.passbolt.mobile.android.scenarios.helpers.createNewPasswordFromHomeScreen
import org.hamcrest.CoreMatchers.endsWith
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.BeforeTest

/**
 * Passbolt - Open source password manager for teams
 * Copyright (c) 2023 Passbolt SA
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
@RunWith(AndroidJUnit4::class)
@MediumTest
class ResourcesDeletionTest : KoinTest {

    @get:Rule
    val startUpActivityRule = lazyActivitySetupScenarioRule<AuthenticationMainActivity>(
        koinOverrideModules = listOf(instrumentationTestsModule),
        intentSupplier = {
            ActivityIntents.authentication(
                InstrumentationRegistry.getInstrumentation().targetContext,
                ActivityIntents.AuthConfig.Startup,
                AppContext.APP,
                managedAccountIntentCreator.getUserLocalId()
            )
        }
    )

    private val managedAccountIntentCreator: ManagedAccountIntentCreator by inject()

    private val resourcesFullRefreshIdlingResource: ResourcesFullRefreshIdlingResource by inject()

    @get:Rule
    val idlingResourceRule = let {
        val signInIdlingResource: SignInIdlingResource by inject()
        val deleteIdlingResource: DeleteResourceIdlingResource by inject()
        val createResourceIdlingResource: CreateResourceIdlingResource by inject()
        IdlingResourceRule(
            arrayOf(
                signInIdlingResource,
                resourcesFullRefreshIdlingResource,
                deleteIdlingResource,
                createResourceIdlingResource
            )
        )
    }

    @BeforeTest
    fun setup() {
        onView(withId(R.id.input)).perform(
            typeText(managedAccountIntentCreator.getUsername()),
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
        )
        onView(withId(R.id.authButton)).perform(click())
        onView(withId(R.id.rootLayout)).check(matches(isDisplayed()))
        createNewPasswordFromHomeScreen()
        onView(withId(R.id.text_input_start_icon)).perform(click())
        onView(withId(R.id.allItems)).perform(click())
    }

    @Test
    //    https://passbolt.testrail.io/index.php?/cases/view/8140
    fun onTheActionMenuDrawerICanClickDeletePasswordElement() {
        //    Given that I am on the resource’s action menu drawer
        onView(withId(R.id.searchEditText)).perform(typeText("ResourcesEditionTestPK"))
        onView(first(withId(R.id.more))).perform(click())
        //    And I see ‘Delete password’ element enabled
        onView(withId(R.id.delete))
            .check(matches(isDisplayed()))
            .check(matches(hasDrawable(id = R.drawable.ic_trash)))
        //    When I click ‘Delete password’
        onView(withId(R.id.delete)).perform(click())
        //    Then I see a popup with ‘Are you sure?’ information
        onView(withText(R.string.are_you_sure)).check(matches(isDisplayed()))
        //    And I see description of this popup
        onView(withText(R.string.resource_will_be_deleted)).check(matches(isDisplayed()))
        //    And I see ‘Cancel’ button in @blue
        onView(withText(R.string.cancel)).check(matches(isDisplayed()))
        //    And I see ‘Delete’ button in @blue
        onView(withText(R.string.delete)).check(matches(isDisplayed()))
    }

    @Test
    //    https://passbolt.testrail.io/index.php?/cases/view/8141
    fun onThePasswordRemovalPopupICanClickTheCancelButton() {
        //    Given that I am on removal popup
        onView(withId(R.id.searchEditText)).perform(typeText("ResourcesEditionTestPK"))
        onView(first(withId(R.id.more))).perform(click())
        onView(withId(R.id.delete)).perform(click())
        //    When I click ‘Cancel’ button in @blue
        onView(withText(R.string.cancel)).perform(click())
        //    Then I am back on the resource view page
        onView(withId(R.id.rootLayout)).check(matches(isDisplayed()))
    }

    @Test
    //    https://passbolt.testrail.io/index.php?/cases/view/8142
    fun onThePasswordRemovalPopupICanClickTheDeleteButton() {
        // unregister refresh idling resource after first refresh not to block the snackbar checks
        // (second refresh is during snackbar is showing)
        IdlingRegistry.getInstance().unregister(resourcesFullRefreshIdlingResource)

        //    Given that I am on removal popup
        onView(withId(R.id.searchEditText)).perform(typeText("ResourcesEditionTestPK"))
        onView(first(withId(R.id.more))).perform(click())
        onView(withId(R.id.delete)).perform(click())
        //    When I click ‘Delete’ button in @blue
        onView(withText(R.string.delete)).perform(click())
        //    Then I am back on the homepage
        onView(withId(R.id.rootLayout)).check(matches(isDisplayed()))
        //    And I see a popup "<password name> password was deleted." in @green
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(endsWith("password was deleted."))))
    }
}
