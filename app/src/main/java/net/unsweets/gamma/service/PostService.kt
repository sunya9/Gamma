package net.unsweets.gamma.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.AndroidInjection
import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.model.PostInputData
import net.unsweets.gamma.domain.usecases.PostUseCase
import javax.inject.Inject

private const val action = "net.unsweets.gamma.service.PostService.SendPost"

class PostService : IntentService("PostService") {
    private enum class IntentKey { PostBody }

    @Inject
    lateinit var postUseCase: PostUseCase

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        val postBody = intent?.getSerializableExtra(IntentKey.PostBody.name) as? PostBody ?: return
        val postOutputData = postUseCase.run(PostInputData(postBody))
        // TODO: return post data?
        postOutputData.res.data
        val resultIntent = Intent().also {
            it.action = action
        }
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(resultIntent)
    }

    companion object {
        @JvmStatic
        fun newIntent(context: Context?, postBody: PostBody) = Intent(context, PostService::class.java).apply {
            putExtra(IntentKey.PostBody.name, postBody)
        }

        fun getIntentFilter() = IntentFilter().also {
            it.addAction(action)
        }
    }
}
