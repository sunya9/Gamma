package net.unsweets.gamma.model.raw

import com.squareup.moshi.Json

data class CrossPost(override val value: CrossPostValue) : Raw.IRaw {
    override val type: String = "io.pnut.core.crosspost"

    data class CrossPostValue(@Json(name = "canonical_url") val canonicalUrl: String) : Raw.RawValue
}