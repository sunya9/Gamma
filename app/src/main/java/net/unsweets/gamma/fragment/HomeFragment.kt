package net.unsweets.gamma.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = StreamViewPagerAdapter(childFragmentManager, context)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    class StreamViewPagerAdapter(fm: FragmentManager, val context: Context?) : FragmentPagerAdapter(fm) {
        private val items = listOf(
            Item(PostItemFragment.getHomeStreamInstance(), R.string.home),
            Item(PostItemFragment.getMentionStreamInstance(), R.string.mentions),
            Item(PostItemFragment.getHomeStreamInstance(), R.string.interactions),
            Item(PostItemFragment.getStarInstance(), R.string.stars)
        )

        data class Item(val fragment: Fragment, @StringRes val title: Int)

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
