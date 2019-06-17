package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LongPost(override val value: LongPostValue) : Raw.IRaw, Parcelable {
    override val type: String = "nl.chimpnut.blog.post"

    @Parcelize
    data class LongPostValue(
        val body: String,
        val title: String?,
        val stamp: String
    ) : Raw.RawValue, Parcelable
}