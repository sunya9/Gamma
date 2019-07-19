package net.unsweets.gamma.presentation.util

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Post

object BindingUtil {
    @JvmStatic
    fun getComposePostTitle(context: Context, replyTarget: Post?): String {
        return if (replyTarget != null) "" else context.getString(R.string.compose_a_post)
    }

    @BindingAdapter("glideSrc")
    @JvmStatic
    fun ImageView.glideSrc(url: String?) {
        if (url.isNullOrEmpty()) {
            setImageDrawable(null)
            return
        }
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

    @BindingAdapter("title")
    @JvmStatic
    fun Toolbar.setTitleBinding(newTitle: Function0<String>) {
        title = newTitle()
    }

    @BindingAdapter("subtitle")
    @JvmStatic
    fun Toolbar.setSubTitleBinding(newSubTitle: Function0<String>) {
        subtitle = newSubTitle()
    }

    @BindingAdapter("backgroundTint")
    @JvmStatic
    fun MaterialButton.setBackgroundTint(@ColorInt color: Int) {
        this.setBackgroundColor(color)
    }

    @BindingAdapter("loading")
    @JvmStatic
    fun MaterialButton.setLoadingIndicator(loading: Boolean) {
        if (loading) {
            val progress = CircularProgressDrawable(context).apply {
                val gray = context.getColor(R.color.colorGrayLighter)
                setColorFilter(gray, PorterDuff.Mode.SRC_IN)
                start()
            }
            icon = progress
        } else {
            icon = null
        }
    }

}

