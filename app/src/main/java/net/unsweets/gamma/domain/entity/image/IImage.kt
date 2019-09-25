package net.unsweets.gamma.domain.entity.image

internal interface IImage {
    val isDefault: Boolean
    val width: Int
    val height: Int
    val link: String
}