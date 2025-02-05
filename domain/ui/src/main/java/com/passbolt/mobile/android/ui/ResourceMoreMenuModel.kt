package com.passbolt.mobile.android.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResourceMoreMenuModel(
    val title: String,
    val canDelete: Boolean,
    val canEdit: Boolean,
    val canShare: Boolean,
    val favouriteOption: FavouriteOption,
    val totpOption: TotpOption
) : Parcelable {

    enum class FavouriteOption {
        ADD_TO_FAVOURITES,
        REMOVE_FROM_FAVOURITES
    }

    enum class TotpOption {
        NONE,
        MANAGE_TOTP,
        ADD_TOTP
    }
}
