package net.unsweets.gamma.model.image

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class Cover(
    @Json(name = "is_default") override val isDefault: Boolean,
    override val height: Int,
    override val link: String,
    override val width: Int
): IImage, Parcelable
