package net.unsweets.gamma.fragment


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_explore.*
import net.unsweets.gamma.R
import net.unsweets.gamma.util.FragmentHelper

class ExploreFragment : PostItemFragment() {
    override fun getListLayout() = R.layout.fragment_explore
    override fun getList(): RecyclerView = list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val streamType = getStreamType()
        toolbar.run {
            if (streamType.titleRes != null) {
                setTitle(streamType.titleRes)
            }
            setNavigationOnClickListener { FragmentHelper.backFragment(fragmentManager) }
        }

    }
}
