package net.unsweets.gamma.presentation.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.DrawerContentFragment


class HomeFragment : Fragment(), DrawerContentFragment {
    private val items: List<Item> = listOf(
        Item(PostItemFragment.getHomeStreamInstance(), R.string.home),
        Item(PostItemFragment.getMentionStreamInstance(), R.string.mentions),
        Item(InteractionFragment.newInstance(), R.string.interactions),
        Item(PostItemFragment.getStarInstance(), R.string.stars)
    )

    override val menuItemId = R.id.home

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = StreamViewPagerAdapter(childFragmentManager, context, items)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: return
                items[position].fragment.scrollToTop()
            }
        })
    }


    data class Item(val fragment: NewBaseListFragment<*, *>, @StringRes val title: Int)

    class StreamViewPagerAdapter(fm: FragmentManager, val context: Context?, private val items: List<Item>) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return items[position].fragment
        }

        override fun getCount(): Int {
            return items.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context?.getString(items[position].title)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
