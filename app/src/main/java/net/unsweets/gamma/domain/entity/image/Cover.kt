package net.unsweets.gamma.domain.entity.image

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Cover(
    @Json(name = "is_default") override val isDefault: Boolean,
    override val width: Int,
    override val height: Int,
    override val link: String
): IImage, Parcelable
