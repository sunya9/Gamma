package net.unsweets.gamma.presentation.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentEditProfileBinding
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.GetProfileInputData
import net.unsweets.gamma.domain.model.io.UpdateProfileInputData
import net.unsweets.gamma.domain.model.io.UpdateUserImageInputData
import net.unsweets.gamma.domain.usecases.GetProfileUseCase
import net.unsweets.gamma.domain.usecases.UpdateProfileUseCase
import net.unsweets.gamma.domain.usecases.UpdateUserImageUseCase
import net.unsweets.gamma.presentation.activity.EditPhotoActivity
import net.unsweets.gamma.presentation.util.ComputedLiveData
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.util.ErrorCollections
import net.unsweets.gamma.util.SingleLiveEvent
import net.unsweets.gamma.util.showAsError
import javax.inject.Inject

class EditProfileFragment : SimpleBottomSheetMenuFragment.Callback,
    GalleryItemListDialogFragment.Listener,
    BaseFragment(), HasAndroidInjector {

    interface Callback {
        fun onRequestToFinish()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onMenuShow(menu: Menu, tag: String?) {
        val imageState = when (tag) {
            DialogKey.Avatar.name -> viewModel.newAvatarUri.value
            DialogKey.Cover.name -> viewModel.newCoverUri.value
            else -> null
        } ?: return
        val menuUndo = menu.findItem(R.id.menuUndo)
        val menuDelete = menu.findItem(R.id.menuDelete)
        val deleteState = when (tag) {
            DialogKey.Avatar.name -> viewModel.user.value?.content?.avatarImage?.isDefault
            DialogKey.Cover.name -> viewModel.user.value?.content?.coverImage?.isDefault
            else -> true
        } ?: true
        when (imageState) {
            is ImageState.Keep -> {
                menuUndo.isVisible = false
                menuDelete.isVisible = !deleteState
            }
            is ImageState.NewImage -> {
                menuUndo.isVisible = true
                menuDelete.isVisible = true
                menuDelete.isVisible = !deleteState
            }
            is ImageState.Delete -> {
                menuUndo.isVisible = true
                menuDelete.isVisible = false
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem, tag: String?) {
        if (tag == null) return
        when (menuItem.itemId) {
            R.id.menuUndo -> undoImage(tag)
            R.id.menuChooseImage -> when (tag) {
                DialogKey.Avatar.name -> changeAvatar()
                DialogKey.Cover.name -> changeCover()
            }
            R.id.menuDelete -> deleteImage(tag)
        }
    }

    private fun undoImage(tag: String) = newImageState(tag, ImageState.Keep)
    private fun deleteImage(tag: String) = newImageState(tag, ImageState.Delete)

    private fun newImageState(tag: String, imageState: ImageState) {
        when (tag) {
            DialogKey.Avatar.name -> viewModel.newAvatarUri.value = imageState
            DialogKey.Cover.name -> viewModel.newCoverUri.value = imageState
        }
    }

    override fun onGalleryItemClicked(uri: Uri, tag: String?) {
        when (tag) {
            DialogKey.Cover.name -> {
                val newIntent = EditPhotoActivity.newIntent(context, uri)
                startActivityForResult(newIntent, RequestCode.Cover.ordinal)
            }
            DialogKey.Avatar.name -> {
                val newIntent = EditPhotoActivity.newIntentSquareMode(context, uri)
                startActivityForResult(newIntent, RequestCode.Avatar.ordinal)
            }
        }
    }

    override fun onShow() {
    }

    override fun onDismiss() {
    }

    private val loadingObserver = Observer<Boolean> {
        binding.toolbar.menu.let { menu ->
            val saveItem = menu.findItem(R.id.menuSave) ?: return@let
            saveItem.isVisible = it == false
        }
    }
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.ShowUpdatePhotoMenu -> showUpdatePhotoMenu(it.imageType)
            is Event.Saved -> saved(it.user)
            is Event.Failed -> showErrorSnackBar(it.t)
        }
    }

    private fun showErrorSnackBar(t: Throwable) {
        val message: String = when (t) {
            is ErrorCollections.CommunicationError -> t.getMessage(context)
            else -> t.localizedMessage
        }
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).showAsError()
    }

    private enum class IntentKey { User }

    private fun saved(user: User) {
        val data = Intent().apply {
            putExtra(IntentKey.User.name, user)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
        finish()
    }

    private fun showUpdatePhotoMenu(imageType: Event.ShowUpdatePhotoMenu.ImageType) {
        val key = when (imageType) {
            Event.ShowUpdatePhotoMenu.ImageType.Avatar -> DialogKey.Avatar
            Event.ShowUpdatePhotoMenu.ImageType.Cover -> DialogKey.Cover
        }
        val fragment = SimpleBottomSheetMenuFragment.newInstance(R.menu.update_photo)
        fragment.show(childFragmentManager, key.name)
    }

    private fun changeCover() {
        val fragment = GalleryItemListDialogFragment.chooseSingle()
        fragment.show(childFragmentManager, DialogKey.Cover.name)
    }

    private fun changeAvatar() {
        val fragment = GalleryItemListDialogFragment.chooseSingle()
        fragment.show(childFragmentManager, DialogKey.Avatar.name)
    }

    private val savingObserver = Observer<Boolean> {

    }
    private lateinit var binding: FragmentEditProfileBinding

    private enum class BundleKey { User }

    private val userId by lazy {
        arguments?.getString(BundleKey.User.name, "") ?: ""
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            EditProfileViewModel.Factory(
                userId,
                getProfileUseCase,
                updateProfileUseCase,
                updateUserImageUseCase
            )
        )[EditProfileViewModel::class.java]
    }

    @Inject
    lateinit var getProfileUseCase: GetProfileUseCase

    @Inject
    lateinit var updateProfileUseCase: UpdateProfileUseCase

    @Inject
    lateinit var updateUserImageUseCase: UpdateUserImageUseCase

    private var listener: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        exitTransition= (MaterialContainerTransform())
        super.onCreate(savedInstanceState)
        viewModel.saving.observe(this, savingObserver)
        viewModel.event.observe(this, eventObserver)
        viewModel.loading.observe(this, loadingObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
//        binding.viewCurrentAvatarImage.setShape(preferenceRepository.shapeOfAvatar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimezoneView()
        setupLocaleView()

        binding.toolbar.setNavigationOnClickListener {
            requestToFinish()
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
                finish()
            }
            RequestCode.Avatar.ordinal -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val res = EditPhotoActivity.parseIntent(data)
                viewModel.newAvatarUri.value = ImageState.NewImage(res.uri)
            }
            RequestCode.Cover.ordinal -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val res = EditPhotoActivity.parseIntent(data)
                viewModel.newCoverUri.value = ImageState.NewImage(res.uri)
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

    private enum class RequestCode { Discard, Avatar, Cover }
    private enum class DialogKey { Discard, Avatar, Cover }

    private val changed: Boolean
        get() =
            viewModel.beforeEditingProfile?.name != viewModel.name.value ||
                    viewModel.beforeEditingProfile?.timezone != viewModel.timezone.value ||
                    viewModel.beforeEditingProfile?.locale != viewModel.locale.value ||
                    viewModel.beforeEditingProfile?.content?.markdownText != viewModel.description.value ||
                    viewModel.newAvatarUri.value !is ImageState.Keep ||
                    viewModel.newCoverUri.value !is ImageState.Keep



    fun requestToFinish(): Boolean {
        return if (changed) {
            val fragment = BasicDialogFragment.Builder()
                .setMessage(R.string.discard_changes)
                .setPositive(R.string.discard)
                .build(RequestCode.Discard.ordinal)
            fragment.show(childFragmentManager, DialogKey.Discard.name)
            true
        } else {
            finish()
            false
        }
    }

    private fun finish() {
        listener?.onRequestToFinish()
    }

    private fun save() {
        Util.hideKeyboard(requireView())
        viewModel.save()
    }

    sealed class Event {
        data class ShowUpdatePhotoMenu(val imageType: ImageType) : Event() {
            enum class ImageType { Avatar, Cover }
        }

        data class Saved(val user: User) : Event()
        data class Failed(val t: Throwable) : Event()
    }

    sealed class ImageState {
        object Keep : ImageState()
        data class NewImage(val uri: Uri) : ImageState()
        object Delete : ImageState()
    }

    class EditProfileViewModel private constructor(
        private val userId: String,
        private val getProfileUseCase: GetProfileUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
        private val updateUserImageUseCase: UpdateUserImageUseCase
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
        val newAvatarUri = MutableLiveData<ImageState>().apply { value = ImageState.Keep }
        val newCoverUri = MutableLiveData<ImageState>().apply { value = ImageState.Keep }
        val coverUri: LiveData<String?> =
            ComputedLiveData.of(user, newCoverUri) { user, newCoverUri ->
                when (newCoverUri) {
                    is ImageState.NewImage -> newCoverUri.uri.path
                    is ImageState.Keep -> user?.content?.coverImage?.link
                    else -> null
                }
            }
        val avatarUri: LiveData<String?> =
            ComputedLiveData.of(user, newAvatarUri) { user, newAvatarUri ->
                when (newAvatarUri) {
                    is ImageState.NewImage -> newAvatarUri.uri.path
                    is ImageState.Keep -> user?.content?.avatarImage?.link
                    else -> null
                }
            }

        init {
            viewModelScope.launch {
                runCatching {
                    getProfileUseCase.run(GetProfileInputData("me"))
                }.onSuccess {
                    beforeEditingProfile = it.res.data
                    user.postValue(it.res.data)
                    name.postValue(it.res.data.name)
                    description.postValue(it.res.data.content.markdownText)
                    timezone.postValue(it.res.data.timezone)
                    locale.postValue(it.res.data.locale)
                    loading.postValue(false)
                }.onFailure {
                    loading.postValue(false)
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
                val avatarTask = async(start = CoroutineStart.LAZY) {
                    when (val newAvatarUriValue = newAvatarUri.value ?: ImageState.Keep) {
                        is ImageState.Keep -> Result.success(null)
                        is ImageState.NewImage -> {
                            runCatching {
                                updateUserImageUseCase.run(
                                    UpdateUserImageInputData(
                                        newAvatarUriValue.uri,
                                        UpdateUserImageInputData.Type.Avatar
                                    )
                                )
                            }
                        }
                        is ImageState.Delete -> {
                            runCatching {
                                updateUserImageUseCase.run(
                                    UpdateUserImageInputData(
                                        null,
                                        UpdateUserImageInputData.Type.Avatar
                                    )
                                )
                            }
                        }
                    }
                }
                val coverTask = async(start = CoroutineStart.LAZY) {
                    when (val newCoverUriValue = newCoverUri.value ?: ImageState.Keep) {
                        ImageState.Keep -> Result.success(null)
                        is ImageState.NewImage -> {
                            runCatching {
                                updateUserImageUseCase.run(
                                    UpdateUserImageInputData(
                                        newCoverUriValue.uri,
                                        UpdateUserImageInputData.Type.Cover
                                    )
                                )
                            }
                        }
                        ImageState.Delete -> {
                            runCatching {
                                updateUserImageUseCase.run(
                                    UpdateUserImageInputData(
                                        null,
                                        UpdateUserImageInputData.Type.Cover
                                    )
                                )
                            }
                        }
                    }
                }
                val profileTask = async(start = CoroutineStart.LAZY) {
                    runCatching {
                        updateProfileUseCase.run(
                            UpdateProfileInputData(
                                name, description, timezone, locale
                            )
                        )
                    }
                }
                withContext(Dispatchers.Default) {
                    avatarTask.await()
                    coverTask.await()
                }
                val avatarRes = avatarTask.await()
                val coverRes = coverTask.await()
                val profileRes = profileTask.await()
                val eventVal =
                    if (avatarRes.isFailure || coverRes.isFailure || profileRes.isFailure) {
                        val t = avatarRes.exceptionOrNull() ?: coverRes.exceptionOrNull()
                        ?: profileRes.exceptionOrNull()
                        t?.let { Event.Failed(it) }
                    } else {
                        profileRes.getOrNull()?.let { Event.Saved(it.user) }
                    }
                eventVal?.let { event.emit(it) }
                loading.postValue(false)
            }
        }

        fun showDialogToChangeAvatar() {
            event.emit(Event.ShowUpdatePhotoMenu(Event.ShowUpdatePhotoMenu.ImageType.Avatar))
        }

        fun showDialogToChangeCover() {
            event.emit(Event.ShowUpdatePhotoMenu(Event.ShowUpdatePhotoMenu.ImageType.Cover))

        }

        class Factory(
            private val userId: String,
            private val getProfileUseCase: GetProfileUseCase,
            private val updateProfileUseCase: UpdateProfileUseCase,
            private val updateUserImageUseCase: UpdateUserImageUseCase
        ) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return EditProfileViewModel(
                    userId,
                    getProfileUseCase,
                    updateProfileUseCase,
                    updateUserImageUseCase
                ) as T
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
