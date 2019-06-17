package net.unsweets.gamma.domain.entity.image

internal interface IImage {
    val isDefault: Boolean
    val height: Int
    val link: String
    val width: Int
}