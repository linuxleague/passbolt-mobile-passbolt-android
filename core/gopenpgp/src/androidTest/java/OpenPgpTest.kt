import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.common.truth.Truth.assertThat
import com.passbolt.mobile.android.core.Gopenpgp.test.R
import com.passbolt.mobile.android.gopenpgp.OpenPgp
import com.passbolt.mobile.android.gopenpgp.exception.OpenPgpException
import com.passbolt.mobile.android.gopenpgp.testOpenPgpModule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertTrue

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

class OpenPgpTest : KoinTest {

    private val openPgp: OpenPgp by inject()

    private lateinit var gracePrivateKey: ByteArray
    private lateinit var gracePublicKey: String

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.ERROR)
        modules(testOpenPgpModule)
    }

    @Before
    fun setup() {
        gracePrivateKey = getInstrumentation().context.resources.openRawResource(R.raw.grace_private_key).readBytes()
        gracePublicKey =
            String(getInstrumentation().context.resources.openRawResource(R.raw.grace_public_key).readBytes())
    }

    @Test
    fun test_messageEncryptionDecryption() = runBlocking {
        val encrypted = openPgp.encryptSignMessageArmored(
            gracePublicKey,
            gracePrivateKey,
            GRACE_KEY_CORRECT_PASSPHRASE,
            PLAIN_MESSAGE
        )
        val decrypted = openPgp.decryptVerifyMessageArmored(
            gracePublicKey,
            gracePrivateKey,
            GRACE_KEY_CORRECT_PASSPHRASE,
            encrypted
        )
        assertThat(String(decrypted)).isEqualTo(PLAIN_MESSAGE)
    }

    @Test
    fun test_messageDecryptionWithIncorrectPassphraseFailsWithCorrectException() = runBlocking {
        val encrypted = openPgp.encryptSignMessageArmored(
            gracePublicKey,
            gracePrivateKey,
            GRACE_KEY_CORRECT_PASSPHRASE,
            PLAIN_MESSAGE
        )

        try {
            val ignored = openPgp.decryptVerifyMessageArmored(
                gracePublicKey,
                gracePrivateKey,
                GRACE_KEY_WRONG_PASSPHRASE,
                encrypted
            )
        } catch (exception: Exception) {
            assertThat(exception).isInstanceOf(OpenPgpException::class.java)
        }
    }

    @Test
    fun test_messageEncryptionWithIncorrectPassphraseFailsWithCorrectException() = runBlocking {
        try {
            val ignored = openPgp.encryptSignMessageArmored(
                gracePublicKey,
                gracePrivateKey,
                GRACE_KEY_WRONG_PASSPHRASE,
                PLAIN_MESSAGE
            )
        } catch (exception: Exception) {
            assertThat(exception).isInstanceOf(OpenPgpException::class.java)
        }
    }

    @Test
    fun test_keyShouldBeUnlockedWithCorrectPassphrase() = runBlocking {
        val isUnlocked = openPgp.unlockKey(
            String(gracePrivateKey),
            GRACE_KEY_CORRECT_PASSPHRASE
        )
        assertTrue(isUnlocked)
    }

    @Test
    fun test_keyShouldNotBeUnlockedWithWrongPassphrase() = runBlocking {
        try {
            val ignored = openPgp.unlockKey(
                String(gracePrivateKey),
                GRACE_KEY_WRONG_PASSPHRASE
            )
        } catch (exception: Exception) {
            assertThat(exception).isInstanceOf(OpenPgpException::class.java)
        }
    }

    private companion object {
        private const val PLAIN_MESSAGE = "test message"
        private val GRACE_KEY_CORRECT_PASSPHRASE = "grace@passbolt.com".toByteArray()
        private val GRACE_KEY_WRONG_PASSPHRASE = "1111".toByteArray()
    }
}
