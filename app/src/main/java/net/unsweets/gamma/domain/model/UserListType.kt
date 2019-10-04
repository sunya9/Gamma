package net.unsweets.gamma.domain.model

sealed class UserListType {
    data class Following(val userId: String) : UserListType()
    data class Followers(val userId: String) : UserListType()
    data class Search(val keyword: String) : UserListType()
    object Blocked : UserListType()
    object Muted : UserListType()
}