package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CrossPost(override val value: CrossPostValue) : Raw.IRaw, Parcelable {
    override val type: String = "io.pnut.core.crosspost"

    @Parcelize
    data class CrossPostValue(@Json(name = "canonical_url") val canonicalUrl: String) : Raw.RawValue, Parcelable
}