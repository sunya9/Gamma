package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import net.unsweets.gamma.R

class PhotoViewActivity : BaseActivity() {
    private enum class IntentKey { Photos }
    companion object {
        fun photoViewInstance(context: Context, items: ArrayList<String>) =
            Intent(context, PhotoViewActivity::class.java).apply {
                putStringArrayListExtra(IntentKey.Photos.name, items)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo_view)
    }

}
