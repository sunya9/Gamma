package net.unsweets.gamma.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_base_list.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.ui.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.ui.base.BaseViewModel

abstract class BaseListFragment<T : Parcelable, V : RecyclerView.ViewHolder> : BaseFragment(),
    BaseViewModel.Refreshable, BaseListRecyclerViewAdapter.IBaseList<T, V> {
//    private enum class StateKey {
//        Items
//    }

    protected lateinit var adapter: BaseListRecyclerViewAdapter<T, V>
    private val items = ArrayList<T>()
    private var listener: OnBaseListListener? = null

    interface OnBaseListListener {
        fun onRefreshed()
    }

    fun scrollToTop() {
        getRecyclerView(view!!).smoothScrollToPosition(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BaseListRecyclerViewAdapter(getBaseListListener(), diffCallback)
//        if(savedInstanceState == null) {
//            getItems().then {
//                items.addAll(0, it.data)
//                adapter.notifyItemRangeInserted(0, it.data.size)
//            }
//        } else {
//            val storedItems = savedInstanceState.getParcelableArrayList<T>(StateKey.Items.name) as ArrayList<T>
//            items.addAll(0, storedItems)
//            adapter.notifyItemRangeInserted(0, storedItems.size)
//        }
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelableArrayList(StateKey.Items.name, items as java.util.ArrayList<out Parcelable>)
    }

    abstract val diffCallback: DiffUtil.ItemCallback<T>


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = (parentFragment ?: context) as? BaseListFragment.OnBaseListListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getFragmentLayout(), container, false)
    }

    protected open fun getFragmentLayout(): Int = R.layout.fragment_base_list
    protected open fun getRecyclerView(view: View): RecyclerView = view.baseList


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecyclerView(view).also {
            it.adapter = adapter
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
            val divider = AppCompatResources.getDrawable(context!!, R.drawable.post_list_divider)!!
            dividerItemDecoration.setDrawable(divider)
            it.addItemDecoration(dividerItemDecoration)
        }

    }

    protected open fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout = view.swipeRefreshLayout


    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    abstract fun getBaseListListener(): BaseListRecyclerViewAdapter.IBaseList<T, V>
}