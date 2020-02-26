package net.unsweets.gamma.mock

import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.CachedList
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

open class PnutCacheRepositoryMock : IPnutCacheRepository {
  override suspend fun getToken(): Token? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun storeToken(token: Token) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun getPosts(streamType: StreamType): CachedList<Post> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun storePosts(
    posts: List<PageableItemWrapper<Post>>,
    streamType: StreamType,
    cacheSize: Int
  ) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun getInteractions(): CachedList<Interaction> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun storeInteractions(
    interactions: List<PageableItemWrapper<Interaction>>,
    cacheSize: Int
  ) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun getUsers(userListType: UserListType): CachedList<User> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun storeUsers(
    users: List<PageableItemWrapper<User>>,
    userListType: UserListType,
    cacheSize: Int
  ) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}