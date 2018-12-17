package net.unsweets.gamma.api

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.unsweets.gamma.api.model.*
import net.unsweets.gamma.model.*
import net.unsweets.gamma.util.PrefManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*


class PnutService {
    companion object {
        private const val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        private const val API_BASE_URL = "https://api.pnut.io/v0/"
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

        fun getService(token: String?): IPnutService = getService(null, token)
        fun getService(context: Context?, token: String?): IPnutService {
            val client = OkHttpClient.Builder()
            token?.let { _token ->
                client.addInterceptor {
                    val request = it.request().newBuilder().addHeader("Authorization", "Bearer $_token").build()
                    it.proceed(request)
                }
            }
            if (context != null) {
                val cache = Cache(context.cacheDir, cacheSize)
                client.cache(cache)
            }
            return Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(IPnutService::class.java)
        }
    }

    interface IPnutService {
        @GET("token")
        fun token(): Call<PnutResponse<Token>>

        // Post resources
        @POST("posts")
        fun createPost(@Body postBody: PostBody): Call<PnutResponse<Post>>

        @PUT("posts/{postId}")
        fun editPost(@Path("postId") postId: String, @Body postBody: PostBody): Call<PnutResponse<Post>>

        @DELETE("posts/{postId}")
        fun deletePost(@Path("postId") postId: String): Call<PnutResponse<Post>>

        @GET("posts/streams/me")
        fun getPersonalStream(): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/unified")
        fun getUnifiedStream(): Call<PnutResponse<List<Post>>>

        @GET("posts/search")
        fun searchPosts(@Query("q") q: String, @QueryMap queries: Map<String, String>): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/global")
        fun getGlobal(): Call<PnutResponse<List<Post>>>

        @GET("posts/tags/{tag}")
        fun getTaggedPosts(@Path("tag") tag: String): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/explore")
        fun getExploreList(): Call<PnutResponse<List<Explore>>>

        @GET("posts/streams/explore/{slug}")
        fun getExplore(@Path("slug") slug: String): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/explore/conversations}")
        fun getConversations(): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/explore/missed_conversations")
        fun getMissedConversations(): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/explore/newcomers")
        fun getNewcomers(): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/explore/photos")
        fun getPhotos(): Call<PnutResponse<List<Post>>>

        @GET("posts/streams/explore/trending")
        fun getTrending(): Call<PnutResponse<List<Post>>>

        @GET("posts/{postId}/thread")
        fun getPostsInThread(@Path("postId") postId: String): Call<PnutResponse<List<Post>>>

        @PUT("posts/{postId}/bookmark")
        fun createStar(@Path("postId") postId: String, @Body note: String): Call<PnutResponse<Post>>

        @DELETE("posts/{postId/bookmark")
        fun deleteStar(@Path("postId") postId: String): Call<PnutResponse<Post>>

        @PUT("posts/{postId}/repost")
        fun createRepost(@Path("postId") postId: String): Call<PnutResponse<Post>>

        @DELETE("posts/{postId/repost")
        fun deleteRepost(@Path("postId") postId: String): Call<PnutResponse<Post>>

        // TODO: I don't know correct type
        @POST("posts/{postId}/report")
        fun reportPost(@Path("postId") postId: String, reason: ReportReason): Call<PnutResponse<Post>>

        @GET("posts/{postId}/interactions")
        fun getPostInteractions(@Path("postId") postId: String, @Query("filters") filters: InteractionFilter?, @Query("exclude") exclude: InteractionFilter?): Call<PnutResponse<List<Interaction>>>

        @GET("posts")
        fun getPosts(@Query("ids") ids: IDs): Call<PnutResponse<List<Post>>>

        @GET("posts/{postId}/revisions")
        fun getRevision(@Path("postId") postId: String): Call<PnutResponse<List<Post>>>

        // user/post resources

        @GET("users/{userId}/posts")
        fun getUserPosts(@Path("userId") userId: String): Call<PnutResponse<List<Post>>>

        @GET("users/{userId}/mentions")
        fun getUserMentions(@Path("userId") userId: String): Call<PnutResponse<List<Post>>>

        @GET("users/me/mentions")
        fun getMentions(): Call<PnutResponse<List<Post>>>

        @GET("users/me/bookmarks")
        fun getStars(): Call<PnutResponse<List<Post>>>

        @GET("users/me/interactions")
        fun getInteractions(): Call<PnutResponse<List<Interaction>>>

        // User(s) resources
        @GET("users/{userId}")
        fun getUser(@Path("userId") userId: String): Call<PnutResponse<User>>

        @PUT("users/me")
        fun putMyProfile(@Body profileBody: ProfileBody): Call<PnutResponse<User>>

        @PATCH("users/me")
        fun patchMyProfile(@Body profileBody: ProfileBody): Call<PnutResponse<User>>

        @GET("users/{userId}/following")
        fun getFollowing(@Path("userId") userId: String): Call<PnutResponse<List<User>>>

        @GET("users/{userId}/followers")
        fun getFollowers(@Path("userId") userId: String): Call<PnutResponse<List<User>>>

        @GET("users/search")
        fun searchUsers(@Query("q") q: String, @QueryMap queries: Map<String, String>): Call<PnutResponse<List<User>>>

        // Channel resources
        @GET("channels/{channelId}")
        fun getChannel(@Path("channelId") channelId: String): Call<PnutResponse<Channel>>

        @GET("users/me/channels")
        fun getChannelsCreateadByMe(): Call<PnutResponse<List<Channel>>>

        @GET("users/me/channels/existing_pm")
        fun getExistingPm(@Query("ids") ids: IDs): Call<PnutResponse<Channel>>

        @GET("users/me/channels/num_unread/pm")
        fun getUnreadPmCount(): Call<PnutResponse<Int>>

        @GET("users/me/channels/subscribed")
        fun getSubscribedChannels(): Call<PnutResponse<List<Channel>>>
    }

    interface Requestable {
        fun pnutService(context: Context): IPnutService {
            val manager = PrefManager(context)
            val token = manager.getDefaultAccountToken()
            return PnutService.getService(context, token)
        }
    }

}

