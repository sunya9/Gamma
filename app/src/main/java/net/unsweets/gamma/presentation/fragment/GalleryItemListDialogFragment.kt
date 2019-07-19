package net.unsweets.gamma.presentation.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContentResolverCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_gallery_item_list_dialog.*
import kotlinx.android.synthetic.main.fragment_gallery_item_list_dialog_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.GlideApp
import java.io.File


class GalleryItemListDialogFragment : BottomSheetDialogFragment() {
    private var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_item_list_dialog, container, false)
    }

    private val galleryItemList: ArrayList<GalleryItem> = ArrayList()
    private val mode by lazy {
        arguments?.getSerializable(BundleKey.Mode.name) as? Mode ?: Mode.Single
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GalleryItemAdapter(galleryItemList)
        Thread(Runnable {
            galleryItemList.addAll(getImages())
            pictureList.post { adapter.notifyDataSetChanged() }
        }).start()
        toolbar.setNavigationOnClickListener { dismiss() }
        pictureList.adapter = adapter
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = (parentFragment ?: context) as Listener
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface Listener {
        fun onGalleryItemClicked(uri: Uri, tag: String?)
        fun onShow()
        fun onDismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.let {
            it.setOnShowListener { listener?.onShow() }
            it.setOnDismissListener { listener?.onDismiss() }
            it.setOnCancelListener { listener?.onDismiss() }
        }
        return dialog
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_gallery_item_list_dialog_item, parent, false)) {

        internal val imageView: ImageView = itemView.imageView

        init {
            imageView.setOnClickListener {
                listener?.let { listener ->
                    listener.onGalleryItemClicked(galleryItemList[adapterPosition].uri, tag)
                    dismiss()
                }
            }
        }
    }

    private fun getImages(): ArrayList<GalleryItem> {
        val res = ArrayList<GalleryItem>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val order = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"
        val resolver = context?.contentResolver
        val cursor = ContentResolverCompat.query(resolver, uri, projection, null, null, order, null)
        while (cursor.moveToNext()) {
            val pathIdx = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            val path = cursor.getString(pathIdx)
            res.add(GalleryItem(Uri.fromFile(File(path))))
        }
        cursor.close()
        return res
    }

    data class GalleryItem(val uri: Uri)

    private inner class GalleryItemAdapter internal constructor(val items: ArrayList<GalleryItem>) :
        RecyclerView.Adapter<ViewHolder>() {
        private inner class ErrorHandling(
            val position: Int
        ) : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                items.removeAt(position)
                this@GalleryItemAdapter.notifyItemRemoved(position)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            GlideApp.with(holder.itemView)
                .load(item.uri)
                .thumbnail(.1f)
                .listener(ErrorHandling(position))
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = items.size
    }

    private enum class Mode { Single, Multiple }
    private enum class BundleKey { Mode }
    companion object {
        fun chooseMultiple() = GalleryItemListDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BundleKey.Mode.name, Mode.Multiple)
            }
        }

        fun chooseSingle() = GalleryItemListDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BundleKey.Mode.name, Mode.Single)
            }
        }
    }
}
