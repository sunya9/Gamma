package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Client(
    var name: String,
    var link: String,
    var id: String
) : Parcelable