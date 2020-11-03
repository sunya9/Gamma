package net.unsweets.gamma.presentation.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_channels.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.adapter.pager.ChannelsPagerAdapter

class ChannelsFragment : BaseFragment() {
    private val adapter by lazy {
        ChannelsPagerAdapter(requireContext(), childFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channels, container, false)
        view.channelsTabLayout.setupWithViewPager(view.channelsViewPager)
        view.toolbar.setNavigationOnClickListener { backToPrevFragment() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.channelsViewPager.adapter = adapter
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}
