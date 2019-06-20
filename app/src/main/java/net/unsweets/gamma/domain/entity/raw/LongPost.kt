package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LongPost(override val value: LongPostValue) : Raw<LongPost.LongPostValue>(), Parcelable {
    override val type: String = "nl.chimpnut.blog.post"

    @Parcelize
    data class LongPostValue(
        val body: String,
        val title: String?,
        val tstamp: String?
    ) : Raw.RawValue, Parcelable
}