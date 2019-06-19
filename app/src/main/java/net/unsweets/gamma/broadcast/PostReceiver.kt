package net.unsweets.gamma.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.service.PostService

class PostReceiver(private val listener: Callback) : BroadcastReceiver() {

    interface Callback {
        fun onPostReceive(post: Post)
        fun onStarReceive(post: Post)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when (PostService.Actions.getAction(action)) {
            PostService.Actions.SendPost -> listener.onPostReceive(PostService.getPost(intent) ?: return)
            PostService.Actions.Star -> listener.onStarReceive(PostService.getPost(intent) ?: return)
        }

    }
}
