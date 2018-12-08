package net.unsweets.gamma.util

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import net.unsweets.gamma.R
import net.unsweets.gamma.model.Post

object BindingUtil {
    @JvmStatic
    fun getComposePostTitle(context: Context, replyTarget: Post?): String {
        return if (replyTarget != null) "" else context.getString(R.string.compose_a_post)
    }

    @BindingAdapter("glideSrc")
    @JvmStatic
    fun ImageView.glideSrc(url: String?) {
        if (url == null) return
        Glide
            .with(this)
            .load(url)
            .into(this)
    }

    @BindingAdapter("textId")
    @JvmStatic
    fun TextView.setTextRes(res: Int?) {
        if(res != null && res > 0) this.setText(res) else this.text = ""
    }
}

