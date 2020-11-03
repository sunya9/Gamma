package net.unsweets.gamma.presentation.fragment


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerDialogFragment
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentComposePollOptionBinding
import net.unsweets.gamma.domain.entity.PollPostBody
import net.unsweets.gamma.domain.model.PollDeadline

class ComposePollOptionFragment : DaggerDialogFragment(), DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> ok()
            DialogInterface.BUTTON_NEGATIVE -> dismiss()
        }
    }

    private fun ok() {
        listener?.ok(
            pollPostBody.copy(
                duration = viewModel.computedPollDeadline.toInt(),
                maxOptions = viewModel.maxOptions.value ?: 1,
                isAnonymous = viewModel.isAnonymous.value ?: true
            )
        )
    }

    private fun setDayRange(range: IntRange) {
        binding.composePollOptionDayNumberPicker.minValue = range.first
        binding.composePollOptionDayNumberPicker.maxValue = range.last
    }

    private fun setHourRange(range: IntRange) {
        binding.composePollOptionHourNumberPicker.minValue = range.first
        binding.composePollOptionHourNumberPicker.maxValue = range.last
    }

    private fun setMinuteRange(range: IntRange) {
        binding.composePollOptionMinNumberPicker.minValue = range.first
        binding.composePollOptionMinNumberPicker.maxValue = range.last
    }

    private lateinit var binding: FragmentComposePollOptionBinding
    private val pollPostBody by lazy {
        arguments?.getParcelable(BundleKey.PollOption.name)
            ?: PollPostBody.defaultValue
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ComposePollOptionViewModel.Factory(pollPostBody)
        )[ComposePollOptionViewModel::class.java]
    }

    interface Callback {
        fun ok(updatedPollPostBody: PollPostBody)
    }

    var listener: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_compose_poll_option,
            view?.findViewById(android.R.id.content),
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupDurationViews()
        setupMaxOptionsView()
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.poll_options)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, this)
            .setNegativeButton(R.string.cancel, this)
            .create()
    }

    private fun setupMaxOptionsView() {
        binding.composePollOptionMaxOptionsNumberPicker.let {
            it.minValue = 1
            it.maxValue = pollPostBody.options.size - 1
            it.setOnValueChangedListener { _, _, newVal -> viewModel.maxOptions.value = newVal }
            it.value = pollPostBody.maxOptions
        }
    }


    private fun setupDurationViews() {
        fixNumberPickerConstraints()
        binding.composePollOptionDayNumberPicker.let {
            it.value = viewModel.day
            it.setOnValueChangedListener { _, _, day ->
                viewModel.day = day
                fixNumberPickerConstraints()
            }
        }


        binding.composePollOptionHourNumberPicker.let {
            it.value = viewModel.hour
            it.setOnValueChangedListener { _, _, hour ->
                viewModel.hour = hour
                fixNumberPickerConstraints()
            }
        }
        binding.composePollOptionMinNumberPicker.let {
            it.value = viewModel.min
            it.setOnValueChangedListener { _, _, newVal ->
                viewModel.min = newVal
            }
        }


    }


    private fun fixNumberPickerConstraints() {
        val duration = viewModel.computedPollDeadline.duration
        setDayRange(0..14)
        when {
            duration >= PollDeadline.maxDuration -> {
                // over 14 days
                setHourRange(0..0)
                setMinuteRange(0..0)
                viewModel.hour = 0
                viewModel.min = 0
            }
            duration <= PollDeadline.minDuration -> {
                setHourRange(0..59)
                setMinuteRange(1..59)
                viewModel.day = 0
                viewModel.hour = 0
            }
            else -> {
                setHourRange(0..23)
                setMinuteRange(0..59)
            }
        }

    }

    class ComposePollOptionViewModel(private val pollPostBody: PollPostBody) :
        ViewModel() {
        var day = pollPostBody.pollDeadline.day
        var hour = pollPostBody.pollDeadline.hour
        var min = pollPostBody.pollDeadline.min
        val isAnonymous = MutableLiveData<Boolean>().apply { value = pollPostBody.isAnonymous }
        val maxOptions = MutableLiveData<Int>().apply { value = pollPostBody.maxOptions }
        val computedPollDeadline
            get() = PollDeadline(day, hour, min)


        class Factory(private val pollPostBody: PollPostBody) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ComposePollOptionViewModel(pollPostBody) as T
            }
        }
    }

    private enum class BundleKey {
        PollOption
    }

    companion object {
        fun newInstance(pollOption: PollPostBody) =
            ComposePollOptionFragment().also {
                it.arguments = Bundle().also { bundle ->
                    bundle.putParcelable(BundleKey.PollOption.name, pollOption)
                }
            }
    }

}
