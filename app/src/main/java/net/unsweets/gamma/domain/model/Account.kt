package net.unsweets.gamma.domain.model

data class Account(
    val id: String,
    val token: String,
    val screenName: String,
    val name: String
) {
    fun getAvatarUrl(size: Int = 96) = "https://api.pnut.io/v0/users/$id/avatar?h=$size&w=$size"
    val screenNameWithAt = "@$screenName"
}