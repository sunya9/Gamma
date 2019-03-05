package net.unsweets.gamma.model.net

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.R
import net.unsweets.gamma.model.entity.Interaction
import net.unsweets.gamma.model.entity.Post
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.model.factory.InteractionDataFactory
import net.unsweets.gamma.model.factory.PostDataFactory
import net.unsweets.gamma.model.factory.UserDataFactory
import net.unsweets.gamma.ui.util.LogUtil
import net.unsweets.gamma.ui.util.PrefManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.Executors

class PnutRepository(val context: Context) {
    private val pageSize = 20
    private val prefManager = PrefManager(context)
    private val pagingConfig = PagedList.Config.Builder().setInitialLoadSizeHint(pageSize).setPageSize(pageSize).build()
    private val executor = Executors.newFixedThreadPool(5)

    sealed class BaseStream(val streamType: Type) {
        enum class Type {
            Home, Mention, Star,
            Conversations, MissedConversations,
            Newcomers, Photos, Trending, Global,
            User, Tag
        }

        object Home : BaseStream(Type.Home)
        object Mention : BaseStream(Type.Mention)
        object Star : BaseStream(Type.Star)
        sealed class ExploreStream(type: Type, @StringRes val titleRes: Int) : BaseStream(type) {
            object Conversations : ExploreStream(Type.Conversations, R.string.conversations)
            object MissedConversations : ExploreStream(Type.MissedConversations, R.string.missed_conversations)
            object Newcomers : ExploreStream(Type.Newcomers, R.string.newcomers)
            object Photos : ExploreStream(Type.Photos, R.string.photos)
            object Trending : ExploreStream(Type.Trending, R.string.trending)
            object Global : ExploreStream(Type.Global, R.string.global)
        }

        sealed class StreamWithArg(type: Type, val extra: String) : BaseStream(type) {
            data class User(val userId: String) : StreamWithArg(Type.User, userId)
            data class Tag(val tag: String) : StreamWithArg(Type.Tag, tag)
        }
    }

    enum class StreamType(@StringRes val titleRes: Int? = null) {
        Home, Mention, Star,
        Conversations(R.string.conversations), MissedConversations(R.string.missed_conversations),
        Newcomers(R.string.newcomers), Photos(R.string.photos), Trending(R.string.trending), Global(R.string.global),
        User, Tag
    }

    enum class UserListMode {
        Following, Followers
    }

    fun getPostItems(postDataFactory: PostDataFactory): LiveData<PagedList<Post>> {
        return LivePagedListBuilder(postDataFactory, pagingConfig).setFetchExecutor(executor)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<Post>() {
                override fun onItemAtFrontLoaded(itemAtFront: Post) {
                    LogUtil.e("Front: ${itemAtFront.id}")
                    super.onItemAtFrontLoaded(itemAtFront)
                }

                override fun onZeroItemsLoaded() {
                    LogUtil.e("onZeroItemsLoaded")
                    super.onZeroItemsLoaded()
                }

            }).build()
    }

    fun getUsers(id: String, mode: UserListMode): LiveData<PagedList<User>> {
        return livePagedListBuilder(UserDataFactory(context, id, mode))
    }

    fun getInteractions(): LiveData<PagedList<Interaction>> {
        return livePagedListBuilder(InteractionDataFactory(context))
    }

    private fun <T> livePagedListBuilder(factory: DataSource.Factory<String, T>): LiveData<PagedList<T>> {
        return LivePagedListBuilder(factory, pagingConfig).setFetchExecutor(executor).build()
    }

    private val apiBaseUrl = "https://api.pnut.io/v0/"
    private val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(
            PolymorphicJsonAdapterFactory.of(Interaction::class.java, "action")
                .withSubtype(Interaction.Repost::class.java, "repost")
                .withSubtype(Interaction.PollResponse::class.java, "poll_response")
                .withSubtype(Interaction.Reply::class.java, "reply")
                .withSubtype(Interaction.Follow::class.java, "follow")
                .withSubtype(Interaction.Bookmark::class.java, "bookmark")
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun getPnutService(token: String?): PnutService {
        val client = OkHttpClient.Builder()
        token?.let { _token ->
            client.addInterceptor {
                val request = it.request().newBuilder().addHeader("Authorization", "Bearer $_token").build()
                it.proceed(request)
            }
        }
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            client.addInterceptor(logging)
        }
        val cache = Cache(context.cacheDir, 1024 * 1024 * 10)
        client.cache(cache)
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(client.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PnutService::class.java)
    }

    val pnutService = getPnutService(prefManager.getDefaultAccountToken())

}
