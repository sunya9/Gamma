package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(
    var name: String,
    var link: String,
    var id: String
) : Parcelable