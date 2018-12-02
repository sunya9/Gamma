package net.unsweets.gamma.model.image

import com.squareup.moshi.Json

class Cover(
    @Json(name = "is_default") override val isDefault: Boolean,
    override val height: Int,
    override val link: String,
    override val width: Int
): IImage
