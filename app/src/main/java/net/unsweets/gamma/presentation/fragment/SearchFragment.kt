package net.unsweets.gamma.presentation.fragment


import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.*
import androidx.viewpager.widget.ViewPager
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentSearchBinding
import net.unsweets.gamma.presentation.util.ShareUtil
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.util.SingleLiveEvent
import java.net.URLEncoder
import java.nio.charset.Charset

class SearchFragment : BaseFragment() {

    private val menuItemClickListener = Toolbar.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.menuSharePostSearch -> sharePostSearchRssUrl()
            else -> return@OnMenuItemClickListener false
        }
        true

    }

    private fun sharePostSearchRssUrl() {
        ShareUtil.launchShareUrlIntent(activity, postSearchRssUrl)
    }

    private val firstSearchObserver = Observer<Boolean> {
        if (!it) return@Observer
        updateMenu(0)
    }
    private val pageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            updateMenu(position)
        }
    }

    private val postSearchRssUrl
        get() = "https://api.pnut.io/v0/feed/rss/posts/search?q=${URLEncoder.encode(
            viewModel.lastKeyword,
            Charset.defaultCharset().name()
        )}"

    private fun updateMenu(position: Int) {
        val searchType = adapter.fragments[position].searchType
        updateMenuItemVisibility(R.id.menuSharePostSearch, searchType == SearchType.Post)
    }

    private fun updateMenuItemVisibility(itemId: Int, visibility: Boolean) {
        val menu = binding.toolbar.menu ?: return
        val menuItem = menu.findItem(itemId) ?: return
        menuItem.isVisible = visibility
    }

    private val adapter by lazy {
        SearchPagerAdapter(requireContext(), childFragmentManager, pagerInfo)
    }
    private lateinit var binding: FragmentSearchBinding
    private var showKeyboardFlag: Boolean = false
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.Search -> {
                hideKeyboard()
                adapter.pagerInfo = updatePagerInfo(it.keyword)
                val position = binding.searchViewPager.currentItem
                binding.searchViewPager.adapter = null
                adapter.notifyDataSetChanged()
                binding.searchViewPager.adapter = adapter
                binding.searchViewPager.currentItem = position
            }
        }
    }
    private val viewModel by lazy {
        ViewModelProvider(this, SearchViewModel.Factory())[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
        viewModel.firstSearch.observe(this, firstSearchObserver)
        if (savedInstanceState != null) {
            savedInstanceState.getParcelable<PagerInfo>(StateKey.PagerInfo.name)?.let { pagerInfo = it }
        }
    }

    private fun updatePagerInfo(keyword: String): PagerInfo {
        pagerInfo = PagerInfo(System.currentTimeMillis(), keyword)
        return pagerInfo
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.searchTabLayout.setupWithViewPager(binding.searchViewPager)
        binding.searchViewPager.addOnPageChangeListener(pageChangeListener)
        binding.toolbar.setNavigationOnClickListener { backToPrevFragment() }
        binding.toolbar.setOnMenuItemClickListener(menuItemClickListener)
        binding.keywordEditText.setOnEditorActionListener { _, actionId, event ->
            when (event) {
                null -> when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        viewModel.search()
                        true
                    }
                    else -> false
                }
                else -> false
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchViewPager.adapter = adapter
        showKeyboard()
    }

    private fun showKeyboard() {
        if (showKeyboardFlag) return
        binding.keywordEditText.post {
            focusToEditText()
        }
        showKeyboardFlag = true
    }

    fun focusToEditText() {
        binding.keywordEditText.requestFocus()
        Util.showKeyboard(binding.keywordEditText)
    }

    private fun hideKeyboard() = Util.hideKeyboard(binding.keywordEditText)

    override fun onDestroyView() {
        hideKeyboard()
        binding.searchViewPager.removeOnPageChangeListener(pageChangeListener)
        super.onDestroyView()
    }

    @Parcelize
    data class PagerInfo(val time: Long, val keyword: String) : Parcelable

    private enum class StateKey { PagerInfo }

    private var pagerInfo: PagerInfo = PagerInfo(System.currentTimeMillis(), "")

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(StateKey.PagerInfo.name, pagerInfo)
    }

    enum class SearchType(val titleRes: Int) { Post(R.string.posts), User(R.string.users) }

    data class FragmentInfo(val fragment: Fragment, val searchType: SearchType)

    class SearchPagerAdapter(
        private val context: Context,
        fm: FragmentManager,
        var pagerInfo: PagerInfo
    ) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        val fragments
            get() = listOf(
                FragmentInfo(PostItemFragment.SearchPostsFragment.newInstance(pagerInfo.keyword), SearchType.Post),
                FragmentInfo(UserListFragment.SearchUserListFragment.newInstance(pagerInfo.keyword), SearchType.User)
            )

        override fun getItem(position: Int): Fragment = fragments[position].fragment

        override fun getPageTitle(position: Int): CharSequence? =
            context.getString(fragments[position].searchType.titleRes)

        override fun getCount(): Int = if (pagerInfo.keyword.isNotEmpty()) fragments.size else 0
        override fun getItemId(position: Int): Long {
            return pagerInfo.time + position
        }
    }

    sealed class Event {
        data class Search(val keyword: String) : Event()
    }

    class SearchViewModel : ViewModel() {
        val keyword = MutableLiveData<String>().apply { value = "" }
        var lastKeyword: String = ""
        val event = SingleLiveEvent<Event>()
        var firstSearch = MutableLiveData<Boolean>().apply { value = false }

        class Factory : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel() as T
            }
        }

        fun search() = keyword.value?.takeIf { it.isNotEmpty() }?.let {
            firstSearch.value = true
            lastKeyword = it
            event.emit(Event.Search(it))
        }

        fun clear() {
            keyword.value = ""
        }

        val clearButtonVisibility = Transformations.map(keyword) {
            if (it.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
