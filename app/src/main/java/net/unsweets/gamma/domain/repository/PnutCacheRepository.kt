package net.unsweets.gamma.domain.repository

import android.content.Context
import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.CachedList
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.presentation.util.PageableItemWrapperConverter
import net.unsweets.gamma.util.LogUtil
import net.unsweets.gamma.util.MoshiSingleton
import java.io.File
import java.io.FileInputStream
import kotlin.math.max


class PnutCacheRepository(currentUserId: String?, context: Context) : IPnutCacheRepository {
    private val baseCacheDir =
        File(getUserCacheDir(context), currentUserId.orEmpty()).apply {
            mkdirs()
        }

    private sealed class CachePath {
        open val name: String = this::class.java.simpleName

        object Token : CachePath()
        data class Stream(val streamType: StreamType) : CachePath() {
            override val name: String = when (streamType) {
                is StreamType.Explore,
                is StreamType.Home,
                is StreamType.Mentions -> streamType::class.java.simpleName
                is StreamType.Stars -> "${streamType::class.java.simpleName}/${streamType.userId}"
                is StreamType.User -> "${streamType::class.java.simpleName}/${streamType.userId}"
                is StreamType.Tag -> "${streamType::class.java.simpleName}/${streamType.tag}"
                is StreamType.Thread -> "${streamType::class.java.simpleName}/${streamType.postId}"
                is StreamType.Search -> "${streamType::class.java.simpleName}/${streamType.keyword}"
            }
        }

        data class User(val userListType: UserListType) : CachePath() {
            private val userId = when (userListType) {
                is UserListType.Followers -> userListType.userId
                is UserListType.Following -> userListType.userId
                else -> "me"
            }
            override val name = "${userListType::class.java.simpleName}/$userId"
        }

        object Interaction : CachePath()
    }

    override suspend fun getToken(): Token? {
        return try {
            val file = CachePath.Token.getFile()
            if (!file.exists()) return null
            val inputStream = FileInputStream(file)
            inputStream.reader().use {
                val json = it.readText()
                TokenJsonAdapter(MoshiSingleton.moshi).fromJson(json)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun CachePath.getFile(): File {
        val file = File("$baseCacheDir/$name")
        LogUtil.e("file $file, ${file.parentFile}")
        file.parentFile?.mkdirs()
        file.createNewFile()
        return file
    }

    override suspend fun storeToken(token: Token) {
        val file = CachePath.Token.getFile()
        val json = TokenJsonAdapter(MoshiSingleton.moshi).toJson(token)
        file.writer().use {
            it.write(json)
        }
    }

    private fun <T : UniquePageable> getList(
        cachePath: CachePath,
        lambda: (jsonStr: String) -> CachedList<T>?
    ): CachedList<T> {
        val emptyList = CachedList<T>(listOf())
        return try {
            val file = cachePath.getFile()
            if (!file.exists()) return emptyList
//            val adapter = cachePath.getCachedListJsonAdapter<T>(modelType)
            val inputStream = FileInputStream(file)
            inputStream.reader().use {
                val jsonStr = it.readText()
                lambda(jsonStr) ?: emptyList
//                val res = adapter.fromJson(jsonStr)
//                res ?: emptyList
            }
        } catch (e: Exception) {
            LogUtil.e("Exception: $e")
            emptyList
        }
    }

    private fun <T : UniquePageable> storeList(
        cachePath: CachePath,
        list: List<PageableItemWrapper<T>>,
        cacheSize: Int,
        lambda: (list: List<PageableItemWrapper<T>>) -> String
    ) {
        val file = cachePath.getFile()
        LogUtil.e("file: $cachePath $file")
        val resizedList = if (cacheSize > 0) {
            list.subList(0, max(list.size, cacheSize))
        } else {
            list
        }
        try {
//            val adapter = cachePath.getCachedListJsonAdapter<T>
//            val cachedList = CachedList(list)
//            val json = adapter.toJson(cachedList)
            val json = lambda(resizedList)
//            LogUtil.e("storeed json: $json")
            file.writer().use {

                it.write(json)
            }
        } catch (e: Exception) {
            LogUtil.e("Exception: $e")
        }

    }

    override suspend fun storePosts(
        posts: List<PageableItemWrapper<Post>>,
        streamType: StreamType,
        cacheSize: Int
    ) {
        return storeList(CachePath.Stream(streamType), posts, cacheSize) {
            val cachedList = PageableItemWrapperConverter.CachedPostList.createFromCachedList(it)
            MoshiSingleton.moshi.adapter(PageableItemWrapperConverter.CachedPostList::class.java)
                .toJson(cachedList)
        }
    }

    override suspend fun storeInteractions(
        interactions: List<PageableItemWrapper<Interaction>>,
        cacheSize: Int
    ) {

        return storeList(CachePath.Interaction, interactions, cacheSize) {
            val cachedList =
                PageableItemWrapperConverter.CachedInteractionList.createFromCachedList(it)
            MoshiSingleton.moshi.adapter(PageableItemWrapperConverter.CachedInteractionList::class.java)
                .toJson(cachedList)
        }
    }

    override suspend fun storeUsers(
        users: List<PageableItemWrapper<User>>,
        userListType: UserListType,
        cacheSize: Int
    ) {
        storeList(CachePath.User(userListType), users, cacheSize) {
            val cachedList = PageableItemWrapperConverter.CachedUserList.createFromCachedList(it)
            MoshiSingleton.moshi.adapter(PageableItemWrapperConverter.CachedUserList::class.java)
                .toJson(cachedList)
        }
    }

    override suspend fun getPosts(streamType: StreamType): CachedList<Post> {
        val res = getList(CachePath.Stream(streamType)) {
            MoshiSingleton.moshi.adapter(PageableItemWrapperConverter.CachedPostList::class.java)
                .fromJson(it)?.toCachedList()
        }
        LogUtil.e("res: ${res.data.size}")
        return res
    }

    override suspend fun getInteractions(): CachedList<Interaction> {
        val res =
            MoshiSingleton.moshi.adapter(PageableItemWrapperConverter.CachedInteractionList::class.java)
        return getList(CachePath.Interaction) {
            res.fromJson(it)?.toCachedList()
        }
    }

    override suspend fun getUsers(userListType: UserListType): CachedList<User> {
        val res = MoshiSingleton.moshi.adapter(
            PageableItemWrapperConverter.CachedUserList::class.java
        )
        return getList(CachePath.User(userListType)) {
            res.fromJson(it)?.toCachedList()
        }
    }

    companion object {
        private const val userCacheDirName = "userCache"
        fun getUserCacheDir(context: Context): File? {
            return File(context.cacheDir, userCacheDirName)
        }
    }
}