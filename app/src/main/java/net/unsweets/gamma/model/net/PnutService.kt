package net.unsweets.gamma.model.net

import net.unsweets.gamma.model.entity.*
import retrofit2.Call
import retrofit2.http.*

interface PnutService {
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
    fun getPersonalStream(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/unified")
    fun getUnifiedStream(): Call<PnutResponse<List<Post>>>

    @GET("posts/search")
    fun searchPosts(@Query("q") q: String, @QueryMap queries: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/global")
    fun getGlobal(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/tags/{tag}")
    fun getTaggedPosts(@Path("tag") tag: String, @QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore")
    fun getExploreList(): Call<PnutResponse<List<Explore>>>

    @GET("posts/streams/explore/{slug}")
    fun getExplore(@Path("slug") slug: String): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/conversations")
    fun getConversations(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/missed_conversations")
    fun getMissedConversations(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/newcomers")
    fun getNewcomers(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/photos")
    fun getPhotos(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/trending")
    fun getTrending(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

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
    fun getUserPosts(@Path("userId") userId: String, @QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("users/{userId}/mentions")
    fun getUserMentions(@Path("userId") userId: String): Call<PnutResponse<List<Post>>>

    @GET("users/me/mentions")
    fun getMentions(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("users/me/bookmarks")
    fun getStars(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("users/me/interactions")
    fun getInteractions(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<Interaction>>>

    // User(s) resources
    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: String): Call<PnutResponse<User>>

    @PUT("users/me")
    fun putMyProfile(@Body profileBody: ProfileBody): Call<PnutResponse<User>>

    @PATCH("users/me")
    fun patchMyProfile(@Body profileBody: ProfileBody): Call<PnutResponse<User>>

    @GET("users/{userId}/following")
    fun getFollowing(@Path("userId") userId: String, @QueryMap paging: Map<String, String>): Call<PnutResponse<List<User>>>

    @GET("users/{userId}/followers")
    fun getFollowers(@Path("userId") userId: String, @QueryMap paging: Map<String, String>): Call<PnutResponse<List<User>>>

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