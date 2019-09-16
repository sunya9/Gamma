package net.unsweets.gamma.presentation.fragment


import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentComposePollBinding
import net.unsweets.gamma.domain.entity.PollPostBody
import net.unsweets.gamma.domain.model.PollDeadline
import net.unsweets.gamma.presentation.adapter.ComposePollListAdapter
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.util.LogUtil
import net.unsweets.gamma.util.SingleLiveEvent

class ComposePollFragment : BaseFragment(), ComposePollOptionFragment.Callback {
    override fun ok(updatedPollPostBody: PollPostBody) {
        viewModel.pollPostBody.value = updatedPollPostBody
    }

    private var listener: Callback? = null
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.AddOption -> addOptionIfPossible()
            is Event.OpenMoreOptions -> openMoreOptions()
        }
    }

    interface Callback {
        fun onDiscardPoll()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    private fun openMoreOptions() {
        val generatedPollPostBody = viewModel.generatedPollPostBody ?: return
        val fragment = ComposePollOptionFragment.newInstance(generatedPollPostBody)
        LogUtil.e("generatedPollPostBody, $generatedPollPostBody")
        fragment.show(childFragmentManager, DialogKey.PollOption.name)
    }

    private enum class DialogKey { PollOption, Discard }

    private fun addOptionIfPossible() {
        composePollListAdapter.addItem()
        LogUtil.e("generatedPollPostBody: ${viewModel.generatedPollPostBody}")
    }

    private lateinit var binding: FragmentComposePollBinding

    val viewModel by lazy {
        ViewModelProvider(
            this
//            ComposePollViewModel.Factory(activity!!.application, pollPostBody)
        )[ComposePollViewModel::class.java]
    }
    private val composePollListAdapter by lazy {
        ComposePollListAdapter(viewModel.items, viewModel.enableAddOptionButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_compose_poll, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.composePollRecyclerView.adapter = composePollListAdapter
        binding.composePollRecyclerView.isNestedScrollingEnabled = false
        binding.composePollToolbar.let {
            AppCompatResources.getColorStateList(it.context, R.color.toolbar_icon_tint)
            val colorStateList =
                resources.getColorStateList(R.color.toolbar_icon_tint, resources.newTheme())
            Util.setTintForToolbarIcon(
                colorStateList,
                binding.composePollToolbar.menu.findItem(R.id.menuDiscardPoll)
            )
            it.setOnMenuItemClickListener {
                discardConfirmation()
                false
            }
            return binding.root
        }
    }

    private enum class RequestCode { Discard }

    private fun discardConfirmation() {
        if (viewModel.generatedPollPostBody?.edited == true) {
            val fragment = BasicDialogFragment.Builder()
                .setMessage(R.string.this_operation_cannot_be_undone)
                .setPositive(R.string.discard)
                .build(RequestCode.Discard.ordinal)
            fragment.show(childFragmentManager, DialogKey.Discard.name)
        } else {
            discard()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.Discard.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                discard()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun discard() {
        viewModel.pollPostBody.value = null
        listener?.onDiscardPoll()
    }

    sealed class Event {
        object AddOption : Event()
        object OpenMoreOptions : Event()
    }

    class ComposePollViewModel(private val app: Application) :
        AndroidViewModel(app) {
        val pollPostBody =
            MutableLiveData<PollPostBody>().apply { value = PollPostBody.defaultValue }
        val prompt = MutableLiveData<String>().apply { value = "" }
        val items = PollPostBody.PollOption.template
        val isAnonymous = Transformations.map(pollPostBody) {
            it?.isAnonymous == true
        }
        val maxOptions = Transformations.map(pollPostBody) {
            it?.maxOptions ?: 1
        }
        val duration = Transformations.map(pollPostBody) {
            it?.duration
        }
        val durationStr = Transformations.map(pollPostBody) {
            if (it == null) return@map ""
            PollDeadline.fromInt(it.duration).toFormatString(app)
        }

        val generatedPollPostBody
            get() = pollPostBody.value?.copy(
                prompt = prompt.value.orEmpty(),
                options = items
            )


        val event = SingleLiveEvent<Event>()
        val enableAddOptionButton = MutableLiveData<Boolean>().apply { value = true }
        fun addOption() = event.emit(Event.AddOption)
        fun openMoreOptions() = event.emit(Event.OpenMoreOptions)

//        class Factory(private val app: Application, private val pollPostBody: PollPostBody) :
//            ViewModelProvider.AndroidViewModelFactory(app) {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return ComposePollViewModel(app) as T
//
//            }
//        }
    }

    companion object {
        fun newInstance() = ComposePollFragment()
    }
}
