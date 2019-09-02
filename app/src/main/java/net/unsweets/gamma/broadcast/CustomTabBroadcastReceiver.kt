package net.unsweets.gamma.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CustomTabBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val url = intent?.dataString ?: return
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)

        val chooserIntent = Intent.createChooser(shareIntent, "Share url")
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context?.startActivity(chooserIntent)
    }
}