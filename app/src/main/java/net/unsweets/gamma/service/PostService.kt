package net.unsweets.gamma.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.AndroidInjection
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.PostBodyOuter
import net.unsweets.gamma.domain.entity.raw.PostRaw
import net.unsweets.gamma.domain.model.io.*
import net.unsweets.gamma.domain.usecases.*
import javax.inject.Inject

private const val actionPrefix = "net.unsweets.gamma.service.PostService"

class PostService : IntentService("PostService") {
    private enum class IntentKey { PostBody, PostId, NewState }
    enum class Actions {
        SendPost, Star, Repost, DeletePost;

        fun getActionName() = "$actionPrefix.$name"

        companion object {
            fun getAction(actionName: String): Actions? {
                return try {
                    valueOf(actionName.split(".").last())
                } catch (e: Exception) {
                    null
                }
            }
        }

    }

    private enum class ResultIntentKey { Post }

    @Inject
    lateinit var postUseCase: PostUseCase
    @Inject
    lateinit var starUseCase: StarUseCase
    @Inject
    lateinit var repostUseCase: RepostUseCase
    @Inject
    lateinit var uploadFileUseCase: UploadFileUseCase
    @Inject
    lateinit var deletePostUseCase: DeletePostUseCase

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let { handleIntent(it) } ?: return
    }

    private fun handleIntent(intent: Intent) {
        val resultIntent = Intent().also {
            it.action = intent.action
        }
        when (intent.action) {
            Actions.SendPost.getActionName() -> {
                val postBodyOuter = intent.getParcelableExtra<PostBodyOuter>(IntentKey.PostBody.name) ?: return
                val raw = mutableListOf<PostRaw<*>>().apply { addAll(postBodyOuter.postBody.raw) }
                val replacementFileRawList = postBodyOuter.files
                    .map { uploadFileUseCase.run(UploadFileInputData(it)).postOEmbedRaw }
                raw.addAll(replacementFileRawList)
                val modifiedPostBody = postBodyOuter.postBody.copy(raw = raw)
                val postOutputData = postUseCase.run(PostInputData(modifiedPostBody))
                resultIntent.putExtra(ResultIntentKey.Post.name, postOutputData.res.data)
            }
            Actions.Star.getActionName() -> {
                val postId = intent.getStringExtra(IntentKey.PostId.name) ?: return
                val newState = intent.getBooleanExtra(IntentKey.NewState.name, true)
                val postOutputData = starUseCase.run(StarInputData(postId, newState))
                resultIntent.putExtra(ResultIntentKey.Post.name, postOutputData.res.data)
            }
            Actions.Repost.getActionName() -> {
                val postId = intent.getStringExtra(IntentKey.PostId.name) ?: return
                val newState = intent.getBooleanExtra(IntentKey.NewState.name, true)
                val postOutputData = repostUseCase.run(
                    RepostInputData(
                        postId,
                        newState
                    )
                )
                resultIntent.putExtra(ResultIntentKey.Post.name, postOutputData.res.data)
            }
            Actions.DeletePost.getActionName() -> {
                val postId = intent.getStringExtra(IntentKey.PostId.name) ?: return
                val postOutputData = deletePostUseCase.run(DeletePostInputData(postId))
                resultIntent.putExtra(ResultIntentKey.Post.name, postOutputData.res.data)
            }
            else -> return
        }
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(resultIntent)
    }

    companion object {
        @JvmStatic
        fun newPostIntent(
            context: Context?,
            postBodyOuter: PostBodyOuter
        ) {
            val intent = Intent(context, PostService::class.java).apply {
                action = Actions.SendPost.getActionName()
                putExtra(IntentKey.PostBody.name, postBodyOuter)
            }
            context?.startService(intent)
        }

        fun newStarIntent(context: Context?, postId: String, newState: Boolean) {
            val intent = Intent(context, PostService::class.java).apply {
                action = Actions.Star.getActionName()
                putExtra(IntentKey.PostId.name, postId)
                putExtra(IntentKey.NewState.name, newState)
            }
            context?.startService(intent)
        }

        fun newRepostIntent(context: Context?, postId: String, newState: Boolean) {
            val intent = Intent(context, PostService::class.java).apply {
                action = Actions.Repost.getActionName()
                putExtra(IntentKey.PostId.name, postId)
                putExtra(IntentKey.NewState.name, newState)
            }
            context?.startService(intent)
        }

        fun getPost(intent: Intent): Post? = intent.getParcelableExtra(ResultIntentKey.Post.name)

        fun getIntentFilter() = IntentFilter().also { intentFilter ->
            Actions.values().forEach {
                intentFilter.addAction(it.getActionName())
            }
        }

        fun newDeletePostIntent(context: Context?, postId: String) {
            val intent = Intent(context, PostService::class.java).apply {
                action = Actions.DeletePost.getActionName()
                putExtra(IntentKey.PostId.name, postId)
            }
            context?.startService(intent)
        }
    }
}
