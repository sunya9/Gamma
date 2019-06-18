package net.unsweets.gamma.domain.repository

import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.params.composed.*
import net.unsweets.gamma.domain.model.params.single.PaginationParam

interface IPnutRepository {
    // posts
    suspend fun getHomeStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getMentionStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getStars(userId: String, getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getUserPosts(userId: String, getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getConversations(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getMissedConversations(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getNewcomers(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getPhotos(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getTrending(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getGlobal(getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun getTagStream(tag: String, getPostsParam: GetPostsParam): PnutResponse<List<Post>>
    suspend fun searchPosts(params: SearchParam): PnutResponse<List<Post>>

    suspend fun createPost(postBody: PostBody): PnutResponse<Post>
    fun createPostSync(postBody: PostBody): PnutResponse<Post>
    suspend fun updatePost(postId: String, postBody: PostBody): PnutResponse<Post>
    suspend fun deletePost(postId: String): PnutResponse<Post>


    // user
    suspend fun getUserProfile(userId: String): PnutResponse<User>
    suspend fun updateMyProfile(profileBody: ProfileBody):  PnutResponse<User>
    suspend fun getFollowing(userId: String, getUsersParam: GetUsersParam): PnutResponse<List<User>>
    suspend fun getFollowers(userId: String, getUsersParam: GetUsersParam): PnutResponse<List<User>>
    suspend fun getBlockedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>>
    suspend fun getMutedUsers(getUsersParam: GetUsersParam): PnutResponse<List<User>>
    suspend fun follow(userId: String): PnutResponse<User>
    suspend fun unFollow(userId: String): PnutResponse<User>
    suspend fun mute(userId: String): PnutResponse<User>
    suspend fun unMute(userId: String): PnutResponse<User>
    suspend fun block(userId: String): PnutResponse<User>
    suspend fun unBlock(userId: String): PnutResponse<User>

    // channel and messages
    suspend fun getChannels(paginationParam: PaginationParam): PnutResponse<List<Channel>>
    suspend fun getMessages(channelId: String, paginationParam: PaginationParam): PnutResponse<List<Message>>

    // others
    suspend fun getInteractions(getInteractionsParam: GetInteractionsParam): PnutResponse<List<Interaction>>

    suspend fun getFiles(getFilesParam: GetFilesParam): PnutResponse<List<File>>
    suspend fun getToken(): PnutResponse<Token>
    suspend fun verifyToken(token: String): PnutResponse<Token>

}