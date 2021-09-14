package com.passbolt.mobile.android.feature.authentication

import com.passbolt.mobile.android.core.navigation.ActivityIntents
import com.passbolt.mobile.android.storage.usecase.selectedaccount.GetSelectedAccountUseCase

class AuthenticationMainPresenter(
    private val getSelectedAccountUseCase: GetSelectedAccountUseCase
) : AuthenticationMainContract.Presenter {

    override var view: AuthenticationMainContract.View? = null

    override fun bundleRetrieved(authConfig: ActivityIntents.AuthConfig, userId: String?) {
        val currentAccount = userId ?: getSelectedAccountUseCase.execute(Unit).selectedAccount

        if (authConfig == ActivityIntents.AuthConfig.SETUP && currentAccount != null) {
            view?.initNavWithoutAccountList(currentAccount)
        } else {
            view?.initNavWithAccountList()
            if (authConfig != ActivityIntents.AuthConfig.MANAGE_ACCOUNT && currentAccount != null) {
                view?.navigateToSignIn(currentAccount)
            }
        }
    }
}
