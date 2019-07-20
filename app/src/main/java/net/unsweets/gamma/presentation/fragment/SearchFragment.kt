package net.unsweets.gamma.presentation.fragment


import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.*
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentSearchBinding
import net.unsweets.gamma.presentation.util.hideKeyboard
import net.unsweets.gamma.presentation.util.showKeyboard
import net.unsweets.gamma.util.SingleLiveEvent

class SearchFragment : BaseFragment() {

    private val adapter by lazy {
        SearchPagerAdapter(context!!, childFragmentManager, pagerInfo)
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
        ViewModelProviders.of(this, SearchViewModel.Factory()).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
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
        binding.toolbar.setNavigationOnClickListener { backToPrevFragment() }
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
        showKeyboard(binding.keywordEditText)
    }

    private fun hideKeyboard() = hideKeyboard(binding.keywordEditText)

    override fun onDestroyView() {
        hideKeyboard()
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

    data class FragmentInfo(val fragment: Fragment, val titleRes: Int)

    class SearchPagerAdapter(
        private val context: Context,
        fm: FragmentManager,
        var pagerInfo: PagerInfo
    ) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments
            get() = listOf(
                FragmentInfo(PostItemFragment.SearchPostsFragment.newInstance(pagerInfo.keyword), R.string.posts),
                FragmentInfo(UserListFragment.SearchUserListFragment.newInstance(pagerInfo.keyword), R.string.users)
            )

        override fun getItem(position: Int): Fragment = fragments[position].fragment

        override fun getPageTitle(position: Int): CharSequence? = context.getString(fragments[position].titleRes)

        override fun getCount(): Int = fragments.size
        override fun getItemId(position: Int): Long {
            return pagerInfo.time + position
        }
    }

    sealed class Event {
        data class Search(val keyword: String) : Event()
    }

    class SearchViewModel : ViewModel() {
        val keyword = MutableLiveData<String>().apply { value = "" }
        val event = SingleLiveEvent<Event>()

        class Factory : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel() as T
            }
        }

        fun search() = keyword.value?.takeIf { it.isNotEmpty() }?.let { event.emit(Event.Search(it)) }
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
