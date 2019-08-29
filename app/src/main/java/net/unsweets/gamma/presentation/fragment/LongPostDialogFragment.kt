package net.unsweets.gamma.presentation.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_long_post_dialog.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.raw.LongPost

class LongPostDialogFragment : BaseBottomSheetDialogFragment() {
    private val longPost: LongPost by lazy {
        arguments?.getParcelable<LongPost>(BundleKey.LongPost.name)
            ?: throw NullPointerException("You must set LongPost")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_long_post_dialog, container, false)
        val title = longPost.value.title
        view.longPostViewToolbar.setNavigationOnClickListener { dismiss() }
        view.longPostViewToolbar.title =
            if (title?.isNotEmpty() == true) title else getString(R.string.long_post_no_title)
        view.longPostBodyTextView.text = longPost.value.body
        return view
    }

    private enum class BundleKey { LongPost }

    companion object {
        fun newInstance(longPost: LongPost) = LongPostDialogFragment().also {
            it.arguments = Bundle().also { bundle ->
                bundle.putParcelable(BundleKey.LongPost.name, longPost)
            }
        }
    }
}
