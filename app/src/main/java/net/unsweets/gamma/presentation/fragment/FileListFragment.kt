package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.files_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.File
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.model.io.GetFilesInputData
import net.unsweets.gamma.domain.model.params.composed.GetFilesParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.usecases.GetFilesUseCase
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.toFormatString
import javax.inject.Inject

class FileListFragment : BaseListFragment<File, FileListFragment.FileViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<File, FileListFragment.FileViewHolder> {
    @Inject
    lateinit var getFilesUseCase: GetFilesUseCase

    override lateinit var viewModel: BaseListViewModel<File>
    override val baseListListener: BaseListRecyclerViewAdapter.IBaseList<File, FileViewHolder> = this
    override val dividerDrawable: Int = R.drawable.divider_full_bleed

    override fun createViewHolder(mView: View, viewType: Int): FileViewHolder = FileViewHolder(mView)

    override fun onClickItemListener(item: File) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, FilesViewModel.Factory(getFilesUseCase)).get(FilesViewModel::class.java)

        super.onCreate(savedInstanceState)
    }

    override fun onBindViewHolder(item: File, viewHolder: FileViewHolder, position: Int) {
        viewHolder.filesItemTitleTextView.text = item.name
        viewHolder.filesItemDateTextView.text = item.createdAt.toFormatString(context)
        viewHolder.filesItemSubTitleTextView.text = item.mimeType
    }

    override fun getItemLayout(): Int = R.layout.files_item

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filesItemTitleTextView: TextView = itemView.filesItemTitleTextView
        val filesItemSubTitleTextView: TextView = itemView.filesItemSubTitleTextView
        val filesItemDateTextView: TextView = itemView.filesItemDateTextView
    }

    class FilesViewModel(private val getFilesUseCase: GetFilesUseCase) : BaseListViewModel<File>() {
        override suspend fun getItems(params: PaginationParam): PnutResponse<List<File>> {
            val getPostParam = GetFilesParam().also { it.add(params) }

            return getFilesUseCase.run(GetFilesInputData(getPostParam)).res
        }

        class Factory(private val getFilesUseCase: GetFilesUseCase) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FilesViewModel(getFilesUseCase) as T
            }


        }
    }

    companion object {
        fun newInstance() = FileListFragment()
    }
}