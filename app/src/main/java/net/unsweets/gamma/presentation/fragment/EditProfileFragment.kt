package net.unsweets.gamma.presentation.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import net.unsweets.gamma.presentation.util.hideKeyboard
import net.unsweets.gamma.util.SingleLiveEvent
import javax.inject.Inject

class EditProfileFragment : DaggerAppCompatDialogFragment() {
    private val loadingObserver = Observer<Boolean> {
        binding.toolbar.menu?.let { menu ->
            val saveItem = menu.findItem(R.id.menuSave) ?: return@let
            saveItem.isVisible = it == false
        }
    }
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.ChangeImage -> changeImage(it.imageType)
            is Event.Saved -> saved(it.user)
        }
    }

    private enum class IntentKey { User }

    private fun saved(user: User) {
        val data = Intent().apply {
            putExtra(IntentKey.User.name, user)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
        dismiss()
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
        viewModel.loading.observe(this, loadingObserver)
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

        setupTimezoneView()
        setupLocaleView()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.Discard.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                dismiss()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLocaleView() {
        PopupMenu(binding.localeLayout.context, binding.localeLayout).also { popupMenu ->
            binding.localeEditText.setOnTouchListener(popupMenu.dragToOpenListener)
            binding.localeEditText.setOnClickListener { popupMenu.show() }
            val locales = resources.getStringArray(R.array.locales)
            locales.iterator().forEach { popupMenu.menu.add(it) }
            popupMenu.setOnMenuItemClickListener {
                viewModel.locale.value = it.title.toString()
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTimezoneView() {
        PopupMenu(binding.timezoneLayout.context, binding.timezoneLayout).also { popupMenu ->
            binding.timezoneEditText.setOnTouchListener(popupMenu.dragToOpenListener)
            binding.timezoneEditText.setOnClickListener { popupMenu.show() }
            val timezones = resources.getStringArray(R.array.timezones)
            timezones.iterator().forEach { popupMenu.menu.add(it) }
            popupMenu.setOnMenuItemClickListener {
                viewModel.timezone.value = it.title.toString()
                true
            }
        }
    }

    private enum class RequestCode { Discard }
    private enum class DialogKey { Discard }

    private val changed: Boolean
        get() =
            viewModel.beforeEditingProfile?.name != viewModel.name.value ||
                    viewModel.beforeEditingProfile?.timezone != viewModel.timezone.value ||
                    viewModel.beforeEditingProfile?.locale != viewModel.locale.value ||
                    viewModel.beforeEditingProfile?.content?.text != viewModel.description.value


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                discard()
                true
            } else {
                false
            }
        }
        return dialog
    }

    private fun discard() {
        if (changed) {
            val fragment = BasicDialogFragment.Builder()
                .setMessage(R.string.discard_changes)
                .setPositive(R.string.discard)
                .build(RequestCode.Discard.ordinal)
            fragment.show(childFragmentManager, DialogKey.Discard.name)
        } else {
            dismiss()
        }
    }

    private fun save() {
        hideKeyboard(view!!)
        viewModel.save()
    }

    sealed class Event {
        data class ChangeImage(val imageType: ImageType) : Event() {
            enum class ImageType { Avatar, Cover }
        }

        data class Saved(val user: User) : Event()
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
        var beforeEditingProfile: User? = null

        init {
            viewModelScope.launch {
                runCatching {
                    getAuthenticatedUseCase.run(Unit)
                }.onSuccess {
                    beforeEditingProfile = it.token.user
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
            val name = this.name.value.orEmpty()
            val description = this.description.value.orEmpty()
            val timezone = this.timezone.value.orEmpty()
            val locale = this.locale.value.orEmpty()
            loading.value = true
            viewModelScope.launch {
                runCatching {
                    updateProfileUseCase.run(
                        UpdateProfileInputData(
                            name, description, timezone, locale
                        )
                    )
                }.onSuccess {
                    event.emit(Event.Saved(it.user))
                }.onFailure {

                }
                loading.value = false
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

        fun parseResultIntent(intent: Intent): User = intent.getParcelableExtra(IntentKey.User.name)
    }
}
