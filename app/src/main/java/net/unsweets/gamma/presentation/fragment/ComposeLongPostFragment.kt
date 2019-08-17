package net.unsweets.gamma.presentation.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentComposeLongPostBinding
import net.unsweets.gamma.domain.entity.raw.LongPost
import net.unsweets.gamma.presentation.util.BackPressedHookable
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.util.SingleLiveEvent
import net.unsweets.gamma.util.observeOnce

class ComposeLongPostFragment : Fragment(), BackPressedHookable {
    override fun onBackPressed() {
        viewModel.back()
    }

    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.Back -> updateLongPost(it)
        }
    }

    private fun updateLongPost(it: Event.Back) {
        val longPost = if (!it.body.isNullOrEmpty()) {
            LongPost(
                LongPost.LongPostValue(
                    it.body, it.title, null
                )
            )
        } else {
            null
        }
        listener?.onUpdateLongPost(longPost)
        (parentFragment as? ComposePostDialogFragment)?.popBackStack()
    }

    private lateinit var binding: FragmentComposeLongPostBinding
    private var listener: Callback? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[ComposeLongPostViewModel::class.java]
    }
    private val longPost by lazy {
        arguments?.getParcelable<LongPost>(BundleKey.LongPost.name)
    }

    private enum class BundleKey { LongPost }

    interface Callback {
        fun onUpdateLongPost(longPost: LongPost?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
        longPost?.let {
            viewModel.body.value = it.value.body
            viewModel.title.value = it.value.title
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_long_post, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bodyEditText.requestFocus()
        viewModel.body.observeOnce(this, Observer {
            binding.bodyEditText.also { view ->
                view.requestFocus()
                view.setSelection(it.length)
            }
        })
        Util.showKeyboard(binding.bodyEditText)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = targetFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    sealed class Event {
        data class Back(val title: String?, val body: String?) : Event()
    }

    class ComposeLongPostViewModel : ViewModel() {
        val title = MutableLiveData<String>()
        val body = MutableLiveData<String>()
        val event = SingleLiveEvent<Event>()
        fun back() = event.emit(Event.Back(title.value, body.value))
    }

    companion object {
        fun newInstance(longPost: LongPost?) = ComposeLongPostFragment().apply {
            arguments = Bundle().apply {
                putParcelable(BundleKey.LongPost.name, longPost)
            }
        }
    }
}
