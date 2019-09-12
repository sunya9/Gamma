package net.unsweets.gamma.domain.repository

import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.CachedList
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.UserListType

interface IPnutCacheRepository {
    suspend fun getToken(): Token?
    suspend fun storeToken(token: Token)
    suspend fun getPosts(streamType: StreamType): CachedList<Post>
    suspend fun storePosts(posts: List<PageableItemWrapper<Post>>, streamType: StreamType)
    suspend fun getInteractions(): CachedList<Interaction>
    suspend fun storeInteractions(interactions: List<PageableItemWrapper<Interaction>>)
    suspend fun getUsers(userListType: UserListType): CachedList<User>
    suspend fun storeUsers(users: List<PageableItemWrapper<User>>, userListType: UserListType)

}