package net.unsweets.gamma.data

import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.params.single.PaginationParam
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
    fun getPersonalStream(@QueryMap params: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/unified")
    fun getUnifiedStream(): Call<PnutResponse<List<Post>>>

    @GET("posts/search")
    fun searchPosts(@QueryMap queries: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/global")
    fun getGlobal(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/tags/{tag}")
    fun getTaggedPosts(@Path("tag") tag: String, @QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore")
    fun getExploreList(): Call<PnutResponse<List<Explore>>>

    @GET("posts/streams/explore/{slug}")
    fun getExplore(@Path("slug") slug: String): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/conversations")
    fun getConversations(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/missed_conversations")
    fun getMissedConversations(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/newcomers")
    fun getNewcomers(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/photos")
    fun getPhotos(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/streams/explore/trending")
    fun getTrending(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("posts/{postId}/thread")
    fun getThread(@Path("postId") postId: String, @QueryMap params: Map<String, String>): Call<PnutResponse<List<Post>>>

    @PUT("posts/{postId}/bookmark")
    fun createStar(@Path("postId") postId: String, @Body note: String = ""): Call<PnutResponse<Post>>

    @DELETE("posts/{postId}/bookmark")
    fun deleteStar(@Path("postId") postId: String): Call<PnutResponse<Post>>

    @PUT("posts/{postId}/repost")
    fun createRepost(@Path("postId") postId: String): Call<PnutResponse<Post>>

    @DELETE("posts/{postId}/repost")
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
    fun getUserPosts(@Path("userId") userId: String, @QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("users/{userId}/mentions")
    fun getUserMentions(@Path("userId") userId: String): Call<PnutResponse<List<Post>>>

    @GET("users/me/mentions")
    fun getMentions(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("users/{userId}/bookmarks")
    fun getStars(@Path("userId") userId: String, @QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Post>>>

    @GET("users/me/interactions")
    fun getInteractions(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<Interaction>>>

    @GET("users/me/blocked")
    fun getBlockedUsers(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<User>>>

    @GET("users/me/muted")
    fun getMutedUsers(@QueryMap pagination: Map<String, String>): Call<PnutResponse<List<User>>>

    // User(s) resources
    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: String): Call<PnutResponse<User>>

    @PUT("users/me")
    fun putMyProfile(@Body profileBody: ProfileBody): Call<PnutResponse<User>>

    @PATCH("users/me")
    fun patchMyProfile(@Body profileBody: ProfileBody): Call<PnutResponse<User>>

    @GET("users/{userId}/following")
    fun getFollowing(@Path("userId") userId: String, @QueryMap pagination: Map<String, String>): Call<PnutResponse<List<User>>>

    @GET("users/{userId}/followers")
    fun getFollowers(@Path("userId") userId: String, @QueryMap pagination: Map<String, String>): Call<PnutResponse<List<User>>>

    @PUT("users/{userId}/follow")
    fun follow(@Path("userId") userId: String): Call<PnutResponse<User>>

    @DELETE("users/{userId}/follow")
    fun unFollow(@Path("userId") userId: String): Call<PnutResponse<User>>

    @PUT("users/{userId}/mute")
    fun mute(@Path("userId") userId: String): Call<PnutResponse<User>>

    @DELETE("users/{userId}/mute")
    fun unMute(@Path("userId") userId: String): Call<PnutResponse<User>>

    @PUT("users/{userId}/block")
    fun block(@Path("userId") userId: String): Call<PnutResponse<User>>

    @DELETE("users/{userId}/block")
    fun unBlock(@Path("userId") userId: String): Call<PnutResponse<User>>

    @GET("users/search")
    fun searchUsers(@Query("q") q: String, @QueryMap queries: Map<String, String>): Call<PnutResponse<List<User>>>

    // Channel resources
    @GET("channels/{channelId}")
    fun getChannel(@Path("channelId") channelId: String): Call<PnutResponse<Channel>>

    @GET("channels/{channelId}/messages")
    fun getChannelMessages(@Path("channelId") channelId: String, @QueryMap paging: Map<String, String>): Call<PnutResponse<List<Message>>>

    @GET("users/me/channels")
    fun getChannelsCreatedByMe(@QueryMap paging: PaginationParam): Call<PnutResponse<List<Channel>>>


    @GET("users/me/channels/existing_pm")
    fun getExistingPm(@Query("ids") ids: IDs): Call<PnutResponse<Channel>>

    @GET("users/me/channels/num_unread/pm")
    fun getUnreadPmCount(): Call<PnutResponse<Int>>

    @GET("users/me/channels/subscribed")
    fun getSubscribedChannels(): Call<PnutResponse<List<Channel>>>

    @GET("users/me/files")
    fun getFiles(@QueryMap paging: Map<String, String>): Call<PnutResponse<List<File>>>
}