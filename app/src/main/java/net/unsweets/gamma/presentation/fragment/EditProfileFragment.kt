package net.unsweets.gamma.presentation.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentEditProfileBinding
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.UpdateProfileInputData
import net.unsweets.gamma.domain.usecases.GetAuthenticatedUserUseCase
import net.unsweets.gamma.domain.usecases.UpdateProfileUseCase
import net.unsweets.gamma.util.SingleLiveEvent
import javax.inject.Inject

class EditProfileFragment : DaggerAppCompatDialogFragment() {
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.ChangeImage -> changeImage(it.imageType)
        }
    }

    private fun changeImage(imageType: Event.ChangeImage.ImageType) {
        when (imageType) {
            Event.ChangeImage.ImageType.Avatar -> changeAvatar()
            Event.ChangeImage.ImageType.Cover -> changeCover()
        }
    }

    private fun changeCover() {
    }

    private fun changeAvatar() {
    }

    private val savingObserver = Observer<Boolean> {

    }
    private lateinit var binding: FragmentEditProfileBinding

    private enum class BundleKey { User }

    private val userId by lazy {
        arguments?.getString(BundleKey.User.name, "") ?: ""
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, EditProfileViewModel.Factory(userId, getAuthenticatedUseCase, updateProfileUseCase))
            .get(EditProfileViewModel::class.java)
    }

    @Inject
    lateinit var getAuthenticatedUseCase: GetAuthenticatedUserUseCase
    @Inject
    lateinit var updateProfileUseCase: UpdateProfileUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        viewModel.saving.observe(this, savingObserver)
        viewModel.event.observe(this, eventObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timezoneAdapter =
            ArrayAdapter.createFromResource(context!!, R.array.timezones, android.R.layout.simple_spinner_dropdown_item)
                .also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        binding.viewTimezoneSpinner.adapter = timezoneAdapter

        val localeAdapter =
            ArrayAdapter.createFromResource(context!!, R.array.locales, android.R.layout.simple_spinner_dropdown_item)
                .also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        binding.viewLocaleSpinner.adapter = localeAdapter
        binding.toolbar.setNavigationOnClickListener {
            discard()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuSave -> save()
            }
            true
        }
    }

    private fun discard() {
        // TODO: show confirmation dialog
        dismiss()
    }

    private fun save() {
        // TODO: show progress bar and save
        viewModel.save()
    }


    sealed class Event {
        data class ChangeImage(val imageType: ImageType) : Event() {
            enum class ImageType { Avatar, Cover }

        }
    }

    class EditProfileViewModel(
        userId: String,
        getAuthenticatedUseCase: GetAuthenticatedUserUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase
    ) : ViewModel() {
        val event = SingleLiveEvent<Event>()
        val loading = MutableLiveData<Boolean>().apply { value = true }
        val name = MutableLiveData<String>()
        val description = MutableLiveData<String>()
        val timezone = MutableLiveData<String>()
        val locale = MutableLiveData<String>()
        val user = MutableLiveData<User>()
        val saving = MutableLiveData<Boolean>()

        init {
            viewModelScope.launch {
                runCatching {
                    getAuthenticatedUseCase.run(Unit)
                }.onSuccess {
                    user.value = it.token.user
                    name.value = it.token.user.name
                    description.value = it.token.user.content.text
                    timezone.value = it.token.user.timezone
                    locale.value = it.token.user.locale
                    loading.value = false
                }.onFailure {
                    loading.value = false
                }
            }
        }

        fun show(show: Boolean) = if (show) View.VISIBLE else View.GONE
        fun save() {
            val name = this.name.value ?: return
            val description = this.description.value ?: return
            val timezone = this.timezone.value ?: return
            val locale = this.locale.value ?: return
            viewModelScope.launch {
                runCatching {
                    updateProfileUseCase.run(
                        UpdateProfileInputData(
                            name, description, timezone, locale
                        )
                    )
                }.onSuccess {
                }.onFailure {

                }
            }
        }

        fun showDialogToChangeAvatar() {
            event.emit(Event.ChangeImage(Event.ChangeImage.ImageType.Avatar))
        }

        fun showDialogToChangeCover() {
            event.emit(Event.ChangeImage(Event.ChangeImage.ImageType.Cover))

        }

        class Factory(
            private val userId: String,
            private val getAuthenticatedUseCase: GetAuthenticatedUserUseCase,
            private val updateProfileUseCase: UpdateProfileUseCase
        ) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return EditProfileViewModel(userId, getAuthenticatedUseCase, updateProfileUseCase) as T
            }

        }
    }

    companion object {
        fun newInstance(userId: String) = EditProfileFragment().apply {
            arguments = Bundle().apply {
                putString(BundleKey.User.name, userId)
            }
        }
    }
}
