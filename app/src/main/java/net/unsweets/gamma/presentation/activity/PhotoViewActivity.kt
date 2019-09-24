package net.unsweets.gamma.presentation.activity

import android.app.Activity
import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_photo_view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.ThumbAndFull
import net.unsweets.gamma.presentation.fragment.PhotoViewItemFragment
import net.unsweets.gamma.util.LogUtil


class PhotoViewActivity : BaseActivity() {
    private enum class IntentKey { Photos, Index }

    companion object {
        private const val singleTransitionName = "singleTransitionName"
        fun photoViewInstance(context: Context?, item: ThumbAndFull) =
            photoViewInstance(context, listOf(item))

        fun startActivity(
            activity: Activity?,
            item: String,
            imageView: ImageView,
            transitionName: String? = null
        ) = startActivity(activity, ThumbAndFull(item, item), imageView, transitionName)
        fun startActivity(
            activity: Activity?,
            item: ThumbAndFull,
            imageView: ImageView,
            transitionName: String? = null
        ) {
            val intent = photoViewInstance(activity, listOf(item))
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair.create(imageView, singleTransitionName)
            )
            LogUtil.e(transitionName)
            activity?.startActivity(intent, options.toBundle())
        }

        fun photoViewInstance(context: Context?, items: List<ThumbAndFull>, index: Int = -1) =
            Intent(context, PhotoViewActivity::class.java).apply {
                putParcelableArrayListExtra(IntentKey.Photos.name, ArrayList(items))
                putExtra(IntentKey.Index.name, index)
            }
    }

    private val photos by lazy {
        intent.getParcelableArrayListExtra<ThumbAndFull>(IntentKey.Photos.name)
    }

    private val index by lazy {
        intent.getIntExtra(IntentKey.Index.name, 0)
    }
    private val adapter by lazy {
        MediaViewPager(supportFragmentManager, photos)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo_view)
        postponeEnterTransition()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mediaViewPager.adapter = adapter
        mediaViewPager.currentItem = index
        mediaviewPagerIndicator.setupWithViewPager(mediaViewPager)

        haulerView.setOnDragDismissedListener {
            finishAfterTransition()
        }

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (names == null || sharedElements == null) return
                val currentItem = mediaViewPager.currentItem
                val view = adapter.getItem(currentItem).view ?: return
                val photoView = view.findViewById<ImageView>(R.id.photoView)
                photoView.transitionName = singleTransitionName
                sharedElements[names[0]] = photoView
            }
        })
    }

    class MediaViewPager(fm: FragmentManager, items: List<ThumbAndFull>) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments = items.map { PhotoViewItemFragment.newInstance(it) }
        override fun getItem(position: Int): Fragment = fragments[position]
        override fun getCount(): Int = fragments.size
    }
}
