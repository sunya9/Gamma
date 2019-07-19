package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_photo_view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.fragment.PhotoViewItemFragment


class PhotoViewActivity : BaseActivity() {
    private enum class IntentKey { Photos, Index }
    companion object {
        fun photoViewInstance(context: Context?, item: String) = photoViewInstance(context, listOf(item))
        fun photoViewInstance(context: Context?, items: List<String>, index: Int = -1) =
            Intent(context, PhotoViewActivity::class.java).apply {
                putStringArrayListExtra(IntentKey.Photos.name, ArrayList(items))
                putExtra(IntentKey.Index.name, index)
            }
    }

    private val photos by lazy {
        intent.getStringArrayListExtra(IntentKey.Photos.name)
    }

    private val index by lazy {
        intent.getIntExtra(IntentKey.Index.name, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val adapter = MediaViewPager(supportFragmentManager, photos)
        mediaViewPager.adapter = adapter
        mediaViewPager.currentItem = index
        mediaviewPagerIndicator.setupWithViewPager(mediaViewPager)

        haulerView.setOnDragDismissedListener {
            finish()
        }
    }


    class MediaViewPager(fm: FragmentManager, items: List<String>) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments = items.map { PhotoViewItemFragment.newInstance(it) }
        override fun getItem(position: Int): Fragment = fragments[position]
        override fun getCount(): Int = fragments.size
    }
}
