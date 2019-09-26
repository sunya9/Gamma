package net.unsweets.gamma.mock

import android.net.Uri
import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.params.composed.GetFilesParam
import net.unsweets.gamma.domain.model.params.composed.GetInteractionsParam
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.util.ErrorCollections
import net.unsweets.sample.Users
import okhttp3.RequestBody
import java.util.*

class PnutRepositoryMock : IPnutRepository {

    private fun <T> success(code: Int = 200, data: () -> T): PnutResponse<T> {
        return PnutResponse(PnutResponse.Meta(code), data())
    }

    private fun <T> failure(code: Int = 400, data: () -> T): PnutResponse<T> {
        return PnutResponse(PnutResponse.Meta(code), data())
    }

    private val errorResponse = ErrorResponse(ErrorResponse.Meta(400, ""))

    override suspend fun getUnifiedStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPersonalStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMentionStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getStars(
        userId: String,
        getPostsParam: GetPostsParam
    ): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserPosts(
        userId: String,
        getPostsParam: GetPostsParam
    ): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getConversations(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMissedConversations(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNewcomers(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPhotos(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTrending(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getGlobal(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTagStream(
        tag: String,
        getPostsParam: GetPostsParam
    ): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun searchPosts(params: GetPostsParam): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getThread(
        postId: String,
        params: GetPostsParam
    ): PnutResponse<List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createPost(postBody: PostBody): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createPostSync(postBody: PostBody, token: String): PnutResponse<Post> {
        return when {
            postBody.text.isEmpty() -> throw Exception()
            token.isEmpty() -> throw Exception()
            else -> success {
                Post(
                    createdAt = Date(),
                    id = "1",
                    content = Post.PostContent(
                        text = postBody.text
                    )
                )
            }
        }
    }

    override suspend fun updatePost(postId: String, postBody: PostBody): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deletePost(postId: String): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createStarPostSync(postId: String): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteStarPostSync(postId: String): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createRepostSync(postId: String): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteRepostSync(postId: String): PnutResponse<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserProfile(userId: String): PnutResponse<User> {
        return when {
            userId.isEmpty() -> throw ErrorCollections.CommunicationError(errorResponse)
            else -> success { Users.getUser(userId) }
        }
    }

    override suspend fun updateMyProfile(profileBody: ProfileBody): PnutResponse<User> {
        val user = Users.getUser("1")
        val content = user.content.copy(text = profileBody.content?.text)
        return success {
            user.copy(
                name = profileBody.name,
                locale = profileBody.locale.orEmpty(),
                timezone = profileBody.timezone.orEmpty(),
                content = content
            )
        }

    }

    override suspend fun getFollowing(
        userId: String,
        getUsersParam: GetUsersParam
    ): PnutResponse<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFollowers(
        userId: String,
        getUsersParam: GetUsersParam
    ): PnutResponse<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getBlockedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMutedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun searchUsers(getSearchUsersParam: GetUsersParam): PnutResponse<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun follow(userId: String): PnutResponse<User> {
        return success {
            val user = Users.getUser(userId)
            if (user.youFollow) throw Exception()
            user.copy(youFollow = true)
        }
    }

    override suspend fun unFollow(userId: String): PnutResponse<User> {
        return success {
            val user = Users.getUser(userId)
            if (!user.youFollow) throw Exception()
            user.copy(youFollow = false)
        }
    }

    override suspend fun mute(userId: String): PnutResponse<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun unMute(userId: String): PnutResponse<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun block(userId: String): PnutResponse<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun unBlock(userId: String): PnutResponse<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateCover(uri: Uri): PnutResponse<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateAvatar(uri: Uri): PnutResponse<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getChannels(paginationParam: PaginationParam): PnutResponse<List<Channel>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMessages(
        channelId: String,
        paginationParam: PaginationParam
    ): PnutResponse<List<Message>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getInteractions(getInteractionsParam: GetInteractionsParam): PnutResponse<List<Interaction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFiles(getFilesParam: GetFilesParam): PnutResponse<List<File>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getToken(): PnutResponse<Token> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun verifyToken(token: String): PnutResponse<Token> {
        return success {
            when (token) {
                "valid" -> {
                    val user = Users.getUser("1")
                    val scopes = Token.Scope.values()
                    val storage = Token.Storage(0, 10)
                    Token(
                        Client("test", "https://example.com/", "test"),
                        listOf(*scopes),
                        user,
                        storage
                    )
                }
                else -> throw Exception()
            }
        }
    }

    override fun createFile(content: RequestBody, fileBody: FileBody): PnutResponse<File> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun updateDefaultPnutService(token: String) {
        // TODO: service mock?
    }

    override fun createPoll(pollPostBody: PollPostBody): PnutResponse<Poll> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPoll(pollId: String, pollToken: String): PnutResponse<Poll> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun vote(
        pollId: String,
        pollToken: String,
        voteBody: VoteBody
    ): PnutResponse<Poll> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}