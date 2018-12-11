package net.unsweets.gamma.model.raw

import com.squareup.moshi.Json
import java.util.*

data class Spoiler(override val value: SpoilerValue) : Raw.IRaw {
    override val type: String = "shawn.spoiler"

    data class SpoilerValue(val topic: String, @Json(name = "expired_at") val expiredAt: Date?) : Raw.RawValue

}