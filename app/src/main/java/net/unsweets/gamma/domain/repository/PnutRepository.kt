package net.unsweets.gamma.domain.repository

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.data.PnutService
import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.entity.raw.*
import net.unsweets.gamma.domain.model.params.composed.*
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.util.await
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class PnutRepository(private val context: Context) : IPnutRepository {
    override fun createRepostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.createRepost(postId).execute().body()!!
    }

    override fun deleteRepostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.deleteRepost(postId).execute().body()!!
    }

    override fun createStarPostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.createStar(postId).execute().body()!!
    }

    override fun deleteStarPostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.deleteStar(postId).execute().body()!!
    }

    override suspend fun getToken(): PnutResponse<Token> {
        return defaultPnutService.token().await()
    }

    override suspend fun searchPosts(params: SearchParam): PnutResponse<List<Post>> {
        return defaultPnutService.searchPosts(params.toMap()).await()
    }

    override suspend fun getTagStream(tag: String, getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getTaggedPosts(tag, getPostsParam.toMap()).await()
    }

    override suspend fun verifyToken(token: String): PnutResponse<Token> {
        return createPnutService(token).token().await()
    }

    override suspend fun getFollowing(userId: String, getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.getFollowing(userId, getUsersParam.toMap()).await()
    }

    override suspend fun getBlockedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.getBlockedUsers(getUsersParam.toMap()).await()
    }

    override suspend fun getMutedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.getMutedUsers(getUsersParam.toMap()).await()
    }

    override suspend fun getHomeStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getPersonalStream(getPostsParam.toMap()).await()
    }

    override suspend fun getMentionStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getMentions(getPostsParam.toMap()).await()
    }

    override suspend fun getStars(userId: String, getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getStars(userId, getPostsParam.toMap()).await()
    }

    override suspend fun getUserPosts(userId: String, getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getUserPosts(userId, getPostsParam.toMap()).await()
    }

    override suspend fun getConversations(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getConversations(getPostsParam.toMap()).await()
    }

    override suspend fun getMissedConversations(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getMissedConversations(getPostsParam.toMap()).await()
    }

    override suspend fun getNewcomers(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getNewcomers(getPostsParam.toMap()).await()
    }

    override suspend fun getPhotos(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getPhotos(getPostsParam.toMap()).await()
    }

    override suspend fun getTrending(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getTrending(getPostsParam.toMap()).await()
    }

    override suspend fun getGlobal(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getGlobal(getPostsParam.toMap()).await()
    }

    override suspend fun createPost(postBody: PostBody): PnutResponse<Post> {
        return defaultPnutService.createPost(postBody).await()
    }

    override fun createPostSync(postBody: PostBody): PnutResponse<Post> {
        return defaultPnutService.createPost(postBody).execute().body()!!
    }

    override suspend fun updatePost(postId: String, postBody: PostBody): PnutResponse<Post> {
        return defaultPnutService.editPost(postId, postBody).await()
    }

    override suspend fun deletePost(postId: String): PnutResponse<Post> {
        return defaultPnutService.deletePost(postId).await()
    }

    override suspend fun getUserProfile(userId: String): PnutResponse<User> {
        return defaultPnutService.getUser(userId).await()
    }

    override suspend fun updateMyProfile(profileBody: ProfileBody): PnutResponse<User> {
        return defaultPnutService.putMyProfile(profileBody).await()
    }

    override suspend fun getFollowers(userId: String, getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.getFollowers(userId, getUsersParam.toMap()).await()
    }

    override suspend fun follow(userId: String): PnutResponse<User> {
        return defaultPnutService.follow(userId).await()
    }

    override suspend fun unFollow(userId: String): PnutResponse<User> {
        return defaultPnutService.unFollow(userId).await()
    }

    override suspend fun mute(userId: String): PnutResponse<User> {
        return defaultPnutService.mute(userId).await()
    }

    override suspend fun unMute(userId: String): PnutResponse<User> {
        return defaultPnutService.unMute(userId).await()
    }

    override suspend fun block(userId: String): PnutResponse<User> {
        return defaultPnutService.block(userId).await()
    }

    override suspend fun unBlock(userId: String): PnutResponse<User> {
        return defaultPnutService.unBlock(userId).await()
    }

    override suspend fun getChannels(paginationParam: PaginationParam): PnutResponse<List<Channel>> {
        return defaultPnutService.getChannelsCreatedByMe(paginationParam).await()
    }

    override suspend fun getMessages(channelId: String, paginationParam: PaginationParam): PnutResponse<List<Message>> {
        return defaultPnutService.getChannelMessages(channelId, paginationParam.toMap()).await()
    }

    override suspend fun getInteractions(getInteractionsParam: GetInteractionsParam): PnutResponse<List<Interaction>> {
        return defaultPnutService.getInteractions(getInteractionsParam.toMap()).await()
    }

    override suspend fun getFiles(getFilesParam: GetFilesParam): PnutResponse<List<File>> {
        return defaultPnutService.getFiles(getFilesParam.toMap()).await()
    }

    private val apiBaseUrl = "https://api.pnut.io/v0/"
    private val cacheSize: Long = 1024 * 1024 * 10
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
        .add(
            PolymorphicJsonAdapterFactory.of(Raw::class.java, "type")
                .withSubtype(OEmbed::class.java, "io.pnut.core.oembed")
                .withSubtype(Spoiler::class.java, "shawn.spoiler")
                .withSubtype(LongPost::class.java, "nl.chimpnut.blog.post")
                .withSubtype(PollNotice::class.java, "io.pnut.core.poll-notice")
                .withDefaultValue(RawImpl())
        )
        .add(
            PolymorphicJsonAdapterFactory.of(OEmbed.BaseOEmbedRawValue::class.java, "type")
                .withSubtype(OEmbed.Photo.PhotoValue::class.java, "photo")
                .withSubtype(OEmbed.Video.VideoValue::class.java, "video")
        )
        .add(KotlinJsonAdapterFactory())
        .build()


    private var defaultPnutService = createPnutService()
    fun updateDefaultPnutService(newToken: String) {
        defaultPnutService = createPnutService(newToken)
    }

    private fun createPnutService(token: String? = null): PnutService {
        val client = OkHttpClient.Builder()
        if (token?.isNotEmpty() == true) client.addInterceptor((getAuthorizationHeaderInterceptor(token)))
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            client.addInterceptor(logging)
        }
        val cache = Cache(context.cacheDir, cacheSize)
        client.cache(cache)
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(client.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PnutService::class.java)
    }

    private fun getAuthorizationHeaderInterceptor(token: String): Interceptor =
        Interceptor {
            val request = it.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            it.proceed(request)
        }
}
