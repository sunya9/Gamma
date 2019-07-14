package net.unsweets.gamma.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.AndroidInjection
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.entity.raw.PostRaw
import net.unsweets.gamma.domain.entity.raw.replacement.PostOEmbed
import net.unsweets.gamma.domain.model.io.PostInputData
import net.unsweets.gamma.domain.model.io.RepostInputData
import net.unsweets.gamma.domain.model.io.StarInputData
import net.unsweets.gamma.domain.model.io.UploadFileInputData
import net.unsweets.gamma.domain.usecases.PostUseCase
import net.unsweets.gamma.domain.usecases.RepostUseCase
import net.unsweets.gamma.domain.usecases.StarUseCase
import net.unsweets.gamma.domain.usecases.UploadFileUseCase
import javax.inject.Inject

private const val actionPrefix = "net.unsweets.gamma.service.PostService"

class PostService : IntentService("PostService") {
    private enum class IntentKey { PostBody, PostId, NewState, Files }
    enum class Actions {
        SendPost, Star, Repost;

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
                val postBody = intent.getSerializableExtra(IntentKey.PostBody.name) as? PostBody ?: return
                val raw = mutableListOf<PostRaw<*>>()
                intent.getParcelableArrayListExtra<Uri>(IntentKey.Files.name)?.let { files ->
                    val replacementFileRawList = files
                        .map { uploadFileUseCase.run(UploadFileInputData(it)) }
                        .map {
                            PostOEmbed(
                                PostOEmbed.OEmbedRawValue(
                                    PostOEmbed.OEmbedRawValue.FileValue(
                                        it.fileId,
                                        it.fileToken
                                    )
                                )
                            )
                        }
                    raw.addAll(replacementFileRawList)
                }
                val modifiedPostBody = postBody.copy(raw = raw)
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
            else -> return
        }
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(resultIntent)
    }

    companion object {
        @JvmStatic
        fun newPostIntent(
            context: Context?,
            postBody: PostBody,
            files: List<Uri> = emptyList(),
            raws: List<PostRaw<*>> = emptyList()
        ) {
            val intent = Intent(context, PostService::class.java).apply {
                action = Actions.SendPost.getActionName()
                putExtra(IntentKey.PostBody.name, postBody)
                putParcelableArrayListExtra(IntentKey.Files.name, ArrayList(files))
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
    }
}
