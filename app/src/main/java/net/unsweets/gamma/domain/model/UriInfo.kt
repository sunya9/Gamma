package net.unsweets.gamma.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UriInfo(
    val uri: Uri
) : Parcelable
