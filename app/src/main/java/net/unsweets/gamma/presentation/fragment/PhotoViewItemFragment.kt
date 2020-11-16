package net.unsweets.gamma.presentation.fragment


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_photo_view_item.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.ThumbAndFull
import net.unsweets.gamma.presentation.util.GlideApp


class PhotoViewItemFragment : Fragment() {

    private val path by lazy {
        arguments?.getParcelable<ThumbAndFull>(BundleKey.Path.name)
            ?: throw NullPointerException("Must set path")
    }
    private val isSharedElementTarget by lazy {
        requireArguments().getBoolean(BundleKey.SharedElementTarget.name, false)
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
        GlideApp.with(view).load(path.full).placeholder(progress)
            .transition(DrawableTransitionOptions.withCrossFade(0))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (isSharedElementTarget)
                        requireActivity().startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource == null) return false
                    val width = resource.intrinsicWidth
                    val height = resource.intrinsicWidth
                    val ratio = height.toFloat() / width.toFloat()
                    val resizedWidth = 100
                    val resizedHeight = (100 * ratio).toInt()
                    val bmp =
                        Bitmap.createBitmap(resizedWidth, resizedHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    resource.setBounds(0, 0, canvas.width, canvas.height)
                    resource.draw(canvas)
                    val palette = Palette.from(bmp).generate()
                    val color = palette.mutedSwatch?.rgb ?: Color.BLACK
                    photoViewWrapper.setBackgroundColor(color)
                    if (isSharedElementTarget)
                        requireActivity().startPostponedEnterTransition()
                    return false
                }
            }).into(photoView)
    }

    private enum class BundleKey { Path, SharedElementTarget }

    companion object {
        fun newInstance(thumbAndFull: ThumbAndFull, isSharedElementTarget: Boolean) =
            PhotoViewItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BundleKey.Path.name, thumbAndFull)
                    putBoolean(BundleKey.SharedElementTarget.name, isSharedElementTarget)
                }
            }
    }
}
