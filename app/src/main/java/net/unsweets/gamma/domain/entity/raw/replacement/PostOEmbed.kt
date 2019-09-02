package net.unsweets.gamma.domain.entity.raw.replacement

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.raw.OEmbed
import net.unsweets.gamma.domain.entity.raw.PostRaw
import net.unsweets.gamma.domain.entity.raw.Raw

@Suppress("UNCHECKED_CAST")
@JsonClass(generateAdapter = true)
@Parcelize
open class PostOEmbed(override val value: OEmbedRawValue) : PostRaw<Raw.RawValue>, Parcelable {
    @IgnoredOnParcel
    override val type: String = OEmbed.type

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class OEmbedRawValue(
        @Json(name = "+io.pnut.core.file") val replacementFileValue: FileValue
    ) : Raw.RawValue, Parcelable {
        @Parcelize
        data class FileValue(
            @Json(name = "file_id") val fileId: String,
            @Json(name = "file_token") val fileToken: String,
            val format: String = "oembed"
        ) : Parcelable
    }
}