package net.unsweets.gamma.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.AndroidInjection
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.model.PostInputData
import net.unsweets.gamma.domain.model.StarInputData
import net.unsweets.gamma.domain.usecases.PostUseCase
import net.unsweets.gamma.domain.usecases.StarUseCase
import javax.inject.Inject

private const val actionPrefix = "net.unsweets.gamma.service.PostService"

class PostService : IntentService("PostService") {
    private enum class IntentKey { PostBody, PostId, NewState }
    enum class Actions {
        SendPost, Star;

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
                val postOutputData = postUseCase.run(PostInputData(postBody))
                // TODO: return post data?
                postOutputData.res.data
            }
            Actions.Star.getActionName() -> {
                val postId = intent.getStringExtra(IntentKey.PostId.name) ?: return
                val newState = intent.getBooleanExtra(IntentKey.NewState.name, true)
                val postOutputData = starUseCase.run(StarInputData(postId, newState))
                resultIntent.putExtra(ResultIntentKey.Post.name, postOutputData.res.data)
            }
            else -> return
        }
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(resultIntent)
    }

    companion object {
        @JvmStatic
        fun newPostIntent(context: Context?, postBody: PostBody) = Intent(context, PostService::class.java).apply {
            action = Actions.SendPost.getActionName()
            putExtra(IntentKey.PostBody.name, postBody)
        }

        fun newStarIntent(context: Context?, postId: String, newState: Boolean) {
            val intent = Intent(context, PostService::class.java).apply {
                action = Actions.Star.getActionName()
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
