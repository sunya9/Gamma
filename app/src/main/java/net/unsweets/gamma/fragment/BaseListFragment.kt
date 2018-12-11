package net.unsweets.gamma.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_base_list.*
import net.unsweets.gamma.R
import net.unsweets.gamma.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.api.PnutResponse
import net.unsweets.gamma.databinding.FragmentBaseListBinding
import net.unsweets.gamma.util.then
import retrofit2.Call

abstract class BaseListFragment<T, V : RecyclerView.ViewHolder> : BaseFragment() {
    private lateinit var adapter: BaseListRecyclerViewAdapter<T, V>
    private val items = ArrayList<T>()
    private var listener: OnBaseListListener? = null

    interface OnBaseListListener {
        fun onRefreshed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = (parentFragment ?: context) as? OnBaseListListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil.inflate<FragmentBaseListBinding>(inflater, R.layout.fragment_base_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.also {
            adapter = BaseListRecyclerViewAdapter(items, getBaseListListener())
            it.adapter = adapter
            val linear = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context, linear.orientation)
            val divider = resources.getDrawable(R.drawable.post_list_divider, context?.theme)
            dividerItemDecoration.setDrawable(divider)
            it.addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun refresh() {
        getItems().then { listener?.onRefreshed() }
    }

    abstract fun getBaseListListener(): BaseListRecyclerViewAdapter.IBaseList<T, V>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getItems().then {
            items.addAll(0, it.data)
            adapter.notifyDataSetChanged()
        }
    }

    abstract fun getItems(): Call<PnutResponse<List<T>>>
}