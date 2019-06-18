package net.unsweets.gamma.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PostReceiver(private val listener: Callback) : BroadcastReceiver() {

    interface Callback {
        fun onReceive()
    }

    override fun onReceive(context: Context, intent: Intent) {
        listener.onReceive()
    }
}
