package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Spoiler(override val value: SpoilerValue) : Raw.IRaw, Parcelable {
    override val type: String = "shawn.spoiler"

    @Parcelize
    data class SpoilerValue(val topic: String, @Json(name = "expired_at") val expiredAt: Date?) : Raw.RawValue,
        Parcelable

}