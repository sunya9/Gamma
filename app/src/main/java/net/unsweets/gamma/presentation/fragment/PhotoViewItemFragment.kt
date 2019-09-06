package net.unsweets.gamma.presentation.fragment


import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_photo_view_item.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.GlideApp

class PhotoViewItemFragment : Fragment() {

    private val path by lazy {
        arguments?.getString(BundleKey.Path.name) ?: throw NullPointerException("Must set path")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_view_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progress = CircularProgressDrawable(view.context).apply {
            val gray = view.context.getColor(R.color.colorGrayLighter)
            setColorFilter(gray, PorterDuff.Mode.SRC_IN)
            start()
            centerRadius = 30f
        }
        GlideApp.with(view).load(path).placeholder(progress)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    activity?.startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    activity?.startPostponedEnterTransition()
                    return false
                }
            }).into(photoView)
    }

    private enum class BundleKey { Path }

    companion object {
        fun newInstance(path: String) = PhotoViewItemFragment().apply {
            arguments = Bundle().apply {
                putString(BundleKey.Path.name, path)
            }
        }
    }
}
