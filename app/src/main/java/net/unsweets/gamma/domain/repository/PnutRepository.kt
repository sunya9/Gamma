package net.unsweets.gamma.domain.repository

import android.content.Context
import android.net.Uri
import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.data.PnutService
import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.params.composed.GetFilesParam
import net.unsweets.gamma.domain.model.params.composed.GetInteractionsParam
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.util.Constants
import net.unsweets.gamma.util.MoshiSingleton
import net.unsweets.gamma.util.await
import net.unsweets.gamma.util.bodyOrThrow
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLConnection
import java.util.*

class PnutRepository(private val context: Context, defaultAccountToken: String? = null) :
    IPnutRepository {
    override suspend fun searchUsers(getSearchUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.searchUsers(getSearchUsersParam.toMap()).await()
    }

    override suspend fun updateCover(uri: Uri): PnutResponse<User> {
        return defaultPnutService.updateCover(createUserImageRequestBody(uri, UserImageKey.Cover))
            .await()
    }

    override suspend fun updateAvatar(uri: Uri): PnutResponse<User> {
        return defaultPnutService.updateAvatar(createUserImageRequestBody(uri, UserImageKey.Avatar))
            .await()
    }

    private enum class UserImageKey { Avatar, Cover }

    private fun createUserImageRequestBody(uri: Uri, key: UserImageKey): MultipartBody.Part {
        val file = java.io.File(uri.path)
        val mimeType = URLConnection.guessContentTypeFromName(file.path)
        val content = RequestBody.create(MediaType.parse(mimeType), file)
        return MultipartBody.Part.createFormData(
            key.name.toLowerCase(Locale.ENGLISH),
            file.name,
            content
        )
    }

    override fun createFile(content: RequestBody, fileBody: FileBody): PnutResponse<File> {
        return defaultPnutService.createFile(
            MultipartBody.Part.createFormData("content", fileBody.name, content),
            RequestBody.create(MediaType.parse("text/plain"), fileBody.name),
            RequestBody.create(MediaType.parse("text/plain"), fileBody.kind.name),
            RequestBody.create(MediaType.parse("text/plain"), BuildConfig.APPLICATION_ID),
            RequestBody.create(MediaType.parse("text/plain"), "true")
        ).execute().bodyOrThrow()
    }

    override suspend fun getThread(
        postId: String,
        params: GetPostsParam
    ): PnutResponse<List<Post>> {
        return defaultPnutService.getThread(postId, params.toMap()).await()
    }

    override fun createRepostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.createRepost(postId).execute().bodyOrThrow()
    }

    override fun deleteRepostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.deleteRepost(postId).execute().bodyOrThrow()
    }

    override fun createStarPostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.createStar(postId).execute().bodyOrThrow()
    }

    override fun deleteStarPostSync(postId: String): PnutResponse<Post> {
        return defaultPnutService.deleteStar(postId).execute().bodyOrThrow()
    }

    override suspend fun getToken(): PnutResponse<Token> {
        return defaultPnutService.token().await()
    }

    override suspend fun searchPosts(params: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.searchPosts(params.toMap()).await()
    }

    override suspend fun getTagStream(
        tag: String,
        getPostsParam: GetPostsParam
    ): PnutResponse<List<Post>> {
        return defaultPnutService.getTaggedPosts(tag, getPostsParam.toMap()).await()
    }

    override suspend fun verifyToken(token: String): PnutResponse<Token> {
        return createPnutService(token).token().await()
    }

    override suspend fun getFollowing(
        userId: String,
        getUsersParam: GetUsersParam
    ): PnutResponse<List<User>> {
        return defaultPnutService.getFollowing(userId, getUsersParam.toMap()).await()
    }

    override suspend fun getBlockedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.getBlockedUsers(getUsersParam.toMap()).await()
    }

    override suspend fun getMutedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        return defaultPnutService.getMutedUsers(getUsersParam.toMap()).await()
    }

    override suspend fun getUnifiedStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getUnifiedStream(getPostsParam.toMap()).await()

    }

    override suspend fun getPersonalStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getPersonalStream(getPostsParam.toMap()).await()
    }

    override suspend fun getMentionStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return defaultPnutService.getMentions(getPostsParam.toMap()).await()
    }

    override suspend fun getStars(
        userId: String,
        getPostsParam: GetPostsParam
    ): PnutResponse<List<Post>> {
        return defaultPnutService.getStars(userId, getPostsParam.toMap()).await()
    }

    override suspend fun getUserPosts(
        userId: String,
        getPostsParam: GetPostsParam
    ): PnutResponse<List<Post>> {
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

    override fun createPostSync(postBody: PostBody, token: String): PnutResponse<Post> {
        return createPnutService(token).createPost(postBody).execute().bodyOrThrow()
    }

    override suspend fun updatePost(postId: String, postBody: PostBody): PnutResponse<Post> {
        return defaultPnutService.editPost(postId, postBody).await()
    }

    override fun deletePost(postId: String): PnutResponse<Post> {
        return defaultPnutService.deletePost(postId).execute().bodyOrThrow()
    }

    override suspend fun getUserProfile(userId: String): PnutResponse<User> {
        return defaultPnutService.getUser(userId).await()
    }

    override suspend fun updateMyProfile(profileBody: ProfileBody): PnutResponse<User> {
        return defaultPnutService.putMyProfile(profileBody).await()
    }

    override suspend fun getFollowers(
        userId: String,
        getUsersParam: GetUsersParam
    ): PnutResponse<List<User>> {
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

    override suspend fun getMessages(
        channelId: String,
        paginationParam: PaginationParam
    ): PnutResponse<List<Message>> {
        return defaultPnutService.getChannelMessages(channelId, paginationParam.toMap()).await()
    }

    override suspend fun getInteractions(getInteractionsParam: GetInteractionsParam): PnutResponse<List<Interaction>> {
        return defaultPnutService.getInteractions(getInteractionsParam.toMap()).await()
    }

    override suspend fun getFiles(getFilesParam: GetFilesParam): PnutResponse<List<File>> {
        return defaultPnutService.getFiles(getFilesParam.toMap()).await()
    }

    override fun createPoll(pollPostBody: PollPostBody): PnutResponse<Poll> {
        return defaultPnutService.createPoll(pollPostBody).execute().bodyOrThrow()
    }

    private val cacheSize: Long = 1024 * 1024 * 10


    private var defaultPnutService = createPnutService(defaultAccountToken)

    // Call this function when change account
    override fun updateDefaultPnutService(token: String) {
        defaultPnutService = createPnutService(token)
    }

    private fun createPnutService(token: String? = null): PnutService {
        val client = OkHttpClient.Builder()
        token?.let { client.addInterceptor((getAuthorizationHeaderInterceptor(it))) }

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            client.addInterceptor(logging)
        }
        val cache = Cache(context.cacheDir, cacheSize)
        client.cache(cache)
        return Retrofit.Builder()
            .baseUrl(Constants.apiBaseUrl)
            .client(client.build())
            .addConverterFactory(MoshiConverterFactory.create(MoshiSingleton.moshi))
            .build()
            .create(PnutService::class.java)
    }

    private fun getAuthorizationHeaderInterceptor(token: String): Interceptor =
        Interceptor {
            val request =
                it.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            it.proceed(request)
        }


}
