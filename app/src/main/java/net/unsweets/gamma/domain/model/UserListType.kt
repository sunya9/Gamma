package net.unsweets.gamma.domain.model

sealed class UserListType {
    object Following: UserListType()
    object Followers: UserListType()
}