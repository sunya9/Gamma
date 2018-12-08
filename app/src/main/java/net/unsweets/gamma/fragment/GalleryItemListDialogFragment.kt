package net.unsweets.gamma.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContentResolverCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_gallery_item_list_dialog.*
import kotlinx.android.synthetic.main.fragment_gallery_item_list_dialog_item.view.*
import net.unsweets.gamma.R

class GalleryItemListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_item_list_dialog, container, false)
    }

    private val galleryItemList: ArrayList<GalleryItem> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GalleryItemAdapter(galleryItemList)
        Thread(Runnable {
            galleryItemList.addAll(getImages())
            list.post { adapter.notifyDataSetChanged() }
        }).start()
        toolbar.setTitle(R.string.gallery)
        list.adapter = adapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    android.R.color.transparent,
                    context?.theme
                )
            )
            val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
            sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    toolbar.alpha = slideOffset
                }
            })
        }
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = (parentFragment ?: context) as Listener
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onGalleryItemClicked(position: String)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_gallery_item_list_dialog_item, parent, false)) {

        internal val imageView: ImageView = itemView.imageView

        init {
            imageView.setOnClickListener {
                mListener?.let { listener ->
                    listener.onGalleryItemClicked(galleryItemList.get(adapterPosition).path)
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
            res.add(GalleryItem(path))
        }
        cursor.close()
        return res
    }

    data class GalleryItem(val path: String)

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
            Glide.with(holder.itemView)
                .load(item.path)
                .thumbnail(.1f)
                .listener(ErrorHandling(position))
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {
        fun newInstance(): GalleryItemListDialogFragment = GalleryItemListDialogFragment()
    }
}
