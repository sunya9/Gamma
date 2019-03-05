package net.unsweets.gamma.ui.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import net.unsweets.gamma.R
import net.unsweets.gamma.model.entity.Post

object BindingUtil {
    @JvmStatic
    fun getComposePostTitle(context: Context, replyTarget: Post?): String {
        return if (replyTarget != null) "" else context.getString(R.string.compose_a_post)
    }

    @BindingAdapter("glideSrc")
    @JvmStatic
    fun ImageView.glideSrc(url: String?) {
        if (url?.isBlank() == true) return
        val placeholder = this.drawable
        val request = GlideApp
            .with(this)
            .load(url)
        if (placeholder != null)
            request.placeholder(placeholder)
        request.into(this)
    }

    @BindingAdapter("textId")
    @JvmStatic
    fun TextView.setTextRes(res: Int?) {
        if(res != null && res > 0) this.setText(res) else this.text = ""
    }

    @BindingAdapter("onNavigationClick")
    @JvmStatic
    fun Toolbar.setOnNavigationClick(listener: View.OnClickListener) {
        setNavigationOnClickListener(listener)
    }
}

