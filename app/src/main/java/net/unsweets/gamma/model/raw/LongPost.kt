package net.unsweets.gamma.model.raw

data class LongPost(override val value: LongPostValue) : Raw.IRaw {
    override val type: String = "nl.chimpnut.blog.post"

    data class LongPostValue(
        val body: String,
        val title: String?,
        val stamp: String
    ) : Raw.RawValue
}