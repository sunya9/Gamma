package net.unsweets.gamma.model.image

internal interface IImage {
    val isDefault: Boolean
    val height: Int
    val link: String
    val width: Int
}