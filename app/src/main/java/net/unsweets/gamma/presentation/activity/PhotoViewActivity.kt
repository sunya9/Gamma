package net.unsweets.gamma.presentation.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_photo_view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.ThumbAndFull
import net.unsweets.gamma.presentation.fragment.PhotoViewItemFragment


class PhotoViewActivity : BaseActivity() {
    private enum class IntentKey { Photos, Index, SharedElementId, Radius, TransitionName }

    companion object {

        fun newIntent(context: Context, url: String, sharedElementId: Int): Intent {
            val items = listOf(ThumbAndFull(url, url))

            return Intent(context, PhotoViewActivity::class.java).apply {
                putParcelableArrayListExtra(IntentKey.Photos.name, ArrayList(items))
                putExtra(IntentKey.SharedElementId.name, sharedElementId)
            }
        }

        fun startActivity(
            activity: Activity?,
            item: String,
            imageView: ImageView,
            radius: Float = 0f,
            transitionName: String = ""
        ) = startActivity(
            activity,
            ThumbAndFull(item, item),
            imageView,
            radius = radius,
            transitionName = transitionName
        )

        fun startActivity(
            activity: Activity?,
            item: ThumbAndFull,
            imageView: ImageView,
            transitionName: String = "",
            radius: Float = 0f
        ) {
            val intent = photoViewInstance(
                activity,
                listOf(item),
                radius = radius,
                transitionName = transitionName
            )
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair.create(imageView, transitionName)
            )
            activity?.startActivity(intent, options.toBundle())
        }

        fun photoViewInstance(
            context: Context?,
            items: List<ThumbAndFull>,
            index: Int = 0,
            radius: Float = 0f,
            transitionName: String = ""
        ) =
            Intent(context, PhotoViewActivity::class.java).apply {
                putParcelableArrayListExtra(IntentKey.Photos.name, ArrayList(items))
                putExtra(IntentKey.Index.name, index)
                putExtra(IntentKey.Radius.name, radius)
                putExtra(IntentKey.TransitionName.name, transitionName)
            }

    }

    private val photos by lazy {
        intent.getParcelableArrayListExtra<ThumbAndFull>(IntentKey.Photos.name)
    }

    private val index by lazy {
        intent.getIntExtra(IntentKey.Index.name, 0)
    }
    private val adapter by lazy {
        MediaViewPager(supportFragmentManager, photos, index)
    }

    private fun fixTopPadding() {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val statusBarHeight = rect.top
        toolbar.layoutParams = FrameLayout.LayoutParams(toolbar.layoutParams).also {
            it.leftMargin = toolbar.marginLeft
            it.bottomMargin = toolbar.marginBottom
            it.rightMargin = toolbar.marginRight
            it.topMargin = statusBarHeight
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        fixTopPadding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupAnimation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        supportPostponeEnterTransition()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mediaViewPager.adapter = adapter
        mediaViewPager.currentItem = index
        mediaviewPagerIndicator.setupWithViewPager(mediaViewPager)

        haulerView.setOnDragDismissedListener {
            finishAfterTransition()
        }
    }


    private fun setupAnimation() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                super.onMapSharedElements(names, sharedElements)
                val view = adapter.getItem(mediaViewPager.currentItem).requireView()
                    .findViewById<View>(R.id.photoView)
                sharedElements[names[0]] = view
            }
        })
    }

    class MediaViewPager(fm: FragmentManager, items: List<ThumbAndFull>, index: Int = 0) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments =
            items.mapIndexed { i, it -> PhotoViewItemFragment.newInstance(it, i == index) }

        override fun getItem(position: Int): Fragment = fragments[position]
        override fun getCount(): Int = fragments.size
    }
}
