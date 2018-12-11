package net.unsweets.gamma.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseListRecyclerViewAdapter<T, V : RecyclerView.ViewHolder>(
    private val mValues: List<T>,
    private val listener: IBaseList<T, V>
) : RecyclerView.Adapter<V>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        val view = LayoutInflater.from(parent.context)
            .inflate(listener.getLayout(), parent, false)
        return listener.createViewHolder(view)
    }

    interface IBaseList<T, V> {
        fun createViewHolder(mView: View): V
        fun onClickItemListener(item: T?)
        fun onBindViewHolder(item: T, viewHolder: V, position: Int)
        fun getLayout(): Int
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = mValues[position]
        listener.onBindViewHolder(item, holder, position)
        listener.onClickItemListener(item)
    }

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int = mValues.size
}
