package net.unsweets.gamma.model.entity

data class ProfileBody(
    val name: String?,
    val content: Content?,
    val timezone: String?,
    val locale: String?
) {
    data class Content(val text: String)
}
