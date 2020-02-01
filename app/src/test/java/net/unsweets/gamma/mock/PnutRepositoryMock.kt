package net.unsweets.gamma.mock

import android.net.Uri
import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.params.composed.GetFilesParam
import net.unsweets.gamma.domain.model.params.composed.GetInteractionsParam
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.sample.Users
import net.unsweets.gamma.util.ErrorCollections
import net.unsweets.gamma.util.TestException
import okhttp3.RequestBody
import java.util.*

open class PnutRepositoryMock(private val pnutMockData: PnutMockData = PnutMockData()) :
    IPnutRepository {
    data class PnutMockData(
        val posts: List<Post> = emptyList(),
        val users: List<User> = emptyList(),
        val channels: List<Channel> = emptyList(),
        val messages: List<Message> = emptyList(),
        val polls: List<Poll> = emptyList(),
        val files: List<File> = emptyList()
    )

    private data class PnutMemoryDB(
        val posts: Map<String, Post> = emptyMap(),
        val users: Map<String, User> = emptyMap(),
        val channels: Map<String, Channel> = emptyMap(),
        val messages: Map<String, Message> = emptyMap(),
        val polls: Map<String, Poll> = emptyMap(),
        val files: Map<String, File> = emptyMap()
    )

    private fun <T : Unique> List<T>.toMap() = this.map { it.uniqueKey to it }.toMap()

    private val pnutMemoryDb by lazy {
        PnutMemoryDB(
            posts = pnutMockData.posts.toMap(),
            users = pnutMockData.users.toMap(),
            channels = pnutMockData.channels.toMap(),
            messages = pnutMockData.messages.toMap(),
            polls = pnutMockData.polls.toMap(),
            files = pnutMockData.files.toMap()
        )
    }

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
            postBody.text.isEmpty() -> throw TestException()
            token.isEmpty() -> throw TestException()
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
            else -> success { pnutMemoryDb.users.getValue(userId) }
        }
    }

    override suspend fun updateMyProfile(profileBody: ProfileBody): PnutResponse<User> {
        // TODO: ???
        val user = Users.me
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
            val user = pnutMemoryDb.users.getValue(userId)
            if (user.youFollow) throw TestException()
            user.copy(youFollow = true)
        }
    }

    override suspend fun unFollow(userId: String): PnutResponse<User> {
        return success {
            val user = pnutMemoryDb.users.getValue(userId)
            if (!user.youFollow) throw TestException()
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
        return success {
            val user = pnutMemoryDb.users.getValue(userId)
            if (user.youBlocked) throw TestException()
            user.copy(youBlocked = true, youFollow = false)
        }
    }

    override suspend fun unBlock(userId: String): PnutResponse<User> {
        return success {
            val user = pnutMemoryDb.users.getValue(userId)
            if (!user.youBlocked) throw TestException()
            user.copy(youBlocked = false, youFollow = false)
        }
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        val poll = pnutMemoryDb.polls.getValue(pollId)
        val newPoll = voteBody.positions.fold(poll) { accPoll, position ->
            val indexedValue = accPoll.options.withIndex().find { it.value.position == position }
                ?: return@fold accPoll
            val newOptionValue = indexedValue.value.copy(isYourResponse = true)
            val newOptions = accPoll.options.toMutableList().also {
                it[indexedValue.index] = newOptionValue
            }
            accPoll.copy(options = newOptions)
        }
        return success { newPoll }
    }
}