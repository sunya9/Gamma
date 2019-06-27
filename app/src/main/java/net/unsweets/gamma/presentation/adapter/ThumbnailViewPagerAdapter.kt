package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.thumbnail_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.raw.OEmbed
import net.unsweets.gamma.domain.entity.raw.Raw
import net.unsweets.gamma.presentation.util.GlideApp

class ThumbnailViewPagerAdapter(private val photos: List<Raw<OEmbed.Photo.PhotoValue>>) :
    RecyclerView.Adapter<ThumbnailViewPagerAdapter.ThumbnailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thumbnail_item, parent, false)
        return ThumbnailViewHolder(view)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = photos[position]
        val url = item.value.thumbnailUrl
        GlideApp.with(context).load(url).into(holder.thumbnailImageView)
    }

    class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImageView: ImageView = itemView.thumbnailImageView
    }
}