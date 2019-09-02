package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Spoiler(override val value: SpoilerValue) : Raw<Spoiler.SpoilerValue>, PostRaw<Spoiler.SpoilerValue>,
    Parcelable {
    @IgnoredOnParcel
    override val type: String = Spoiler.type

    @Parcelize
    data class SpoilerValue(val topic: String, @Json(name = "expired_at") val expiredAt: Date?) : Raw.RawValue,
        Parcelable

    companion object {
        fun getSpoilerRaw(rawList: List<Raw<*>>): Spoiler? = rawList.find { it is Spoiler } as? Spoiler
        const val type = "shawn.spoiler"
    }
}