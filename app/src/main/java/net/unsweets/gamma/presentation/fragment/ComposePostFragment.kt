package net.unsweets.gamma.presentation.fragment


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.compose_thumbnail_image.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentComposePostBinding
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.entity.PostBodyOuter
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.entity.raw.LongPost
import net.unsweets.gamma.domain.entity.raw.PostRaw
import net.unsweets.gamma.domain.entity.raw.Spoiler
import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.model.UriInfo
import net.unsweets.gamma.domain.usecases.GetCurrentAccountUseCase
import net.unsweets.gamma.presentation.activity.EditPhotoActivity
import net.unsweets.gamma.presentation.util.AnimationCallback
import net.unsweets.gamma.presentation.util.BackPressedHookable
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.service.PostService
import net.unsweets.gamma.util.Constants
import net.unsweets.gamma.util.SingleLiveEvent
import net.unsweets.gamma.util.observeOnce
import java.util.*
import javax.inject.Inject

class ComposePostFragment : BaseFragment(), GalleryItemListDialogFragment.Listener,
    AnimationCallback,
    BackPressedHookable, ComposeLongPostFragment.Callback, SpoilerDialogFragment.Callback,
    ChangeAccountDialogFragment.Callback, ComposePollFragment.Callback {

    override fun onDiscardPoll() {
        viewModel.enablePoll.value = false
        updatePollMenuItem()
    }

    override fun changeAccount(account: Account) {
        viewModel.currentUserIdLiveData.value = account.id
    }

    override fun onUpdateLongPost(longPost: LongPost?) {
        viewModel.longPost = longPost
    }

    override fun onUpdateSpoiler(spoiler: Spoiler?) {
        viewModel.spoiler = spoiler
        updateSpoilerMenuItem()
    }

    override fun onAnimationEnd(open: Boolean) {
        if (open) {
            focusToEditText()
        }
    }

    override fun onBackPressed() {
        cancelToCompose()
    }

    override fun onAnimationStart(open: Boolean) {
        if (!open) {
            Util.hideKeyboard(binding.composeTextEditText)
        }
    }

    interface Callback {
        fun onFinish()
        fun addFragment(fragment: Fragment)
    }

    private enum class BundleKey {
        ReplyTarget, InitialText, InitialPhoto
    }

    private enum class DialogKey {
        Gallery, Discard, Spoiler, Accounts
    }

    private enum class PermissionRequestCode {
        Storage
    }

    private enum class RequestCode { EditPhoto, Discard }

    private val enablePollObserver = Observer<Boolean> {
        updatePollMenuItem()
        togglePollView(it == true)
    }

    private val pollFragment: ComposePollFragment?
        get() = childFragmentManager.findFragmentById(R.id.pollLayout) as? ComposePollFragment

    private fun togglePollView(it: Boolean) {
        val ft = childFragmentManager.beginTransaction()
        if (it) {
            ft
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.pollLayout, ComposePollFragment.newInstance())
        } else {
            pollFragment?.let { ft.remove(it) }
        }
        ft.commit()

    }

    private var listener: Callback? = null
    private val thumbnailAdapterListener = object : ThumbnailAdapter.Callback {
        override fun updateList(list: List<UriInfo>) {
            viewModel.media = list.toMutableList()
        }

        override fun onClick(uri: Uri, index: Int) {
            val newIntent = EditPhotoActivity.newIntent(context, uri, index)
            startActivityForResult(newIntent, RequestCode.EditPhoto.ordinal)
        }

        override fun onRemove() {
            if (adapter.getItems().size > 0) return
            viewModel.previewAttachmentsVisibility.value = View.GONE
        }
    }

    private val counterObserver = Observer<Int> {
        updateSendMenuItem()
    }

    private fun updateSendMenuItem() {
        val menuItem = findMenuItemWithinRightMenu(R.id.menuPost) ?: return
        val count = viewModel.counter.value ?: 0
        val enabled = (0 <= count) && count < Constants.MaxPostTextLength
        menuItem.isEnabled = enabled
    }

    private fun findMenuItemWithinRightMenu(menuId: Int): MenuItem? {
        val menu = binding.viewRightActionMenuView.menu ?: return null
        return menu.findItem(menuId)
    }

    private fun findMenuItemWithinLeftMenu(menuId: Int): MenuItem? {
        val menu = binding.viewLeftActionMenuView.menu ?: return null
        return menu.findItem(menuId)
    }

    private fun updateNsfwMenuItem() {
        val nsfwMenuItem = findMenuItemWithinLeftMenu(R.id.menuNsfw) ?: return
        val nsfwFlag = viewModel.nsfw.value ?: false
        nsfwMenuItem.isChecked = nsfwFlag
        Util.setTintForCheckableMenuItem(context!!, nsfwMenuItem)
    }


    private fun syncMenuState() {
        updateSendMenuItem()
        updateNsfwMenuItem()
        updateSpoilerMenuItem()
        updatePollMenuItem()
    }

    private fun updatePollMenuItem() {
        val pollMenuItem = findMenuItemWithinLeftMenu(R.id.menuPoll) ?: return
        pollMenuItem.isChecked = viewModel.enablePoll.value == true
        Util.setTintForCheckableMenuItem(context!!, pollMenuItem)
    }

    private fun updateSpoilerMenuItem() {
        val spoilerMenuItem = findMenuItemWithinLeftMenu(R.id.menuSpoiler) ?: return
        spoilerMenuItem.isChecked = viewModel.spoiler != null
        Util.setTintForCheckableMenuItem(context!!, spoilerMenuItem)
    }

    private val viewModel: ComposePostViewModel by lazy {
        ViewModelProvider(
            this,
            ComposePostViewModel.Factory(
                replyTarget,
                mentionToMyself,
                initialText,
                currentUserId
            )
        )[ComposePostViewModel::class.java]
    }

    private val replyTarget: Post? by lazy {
        arguments?.getParcelable<Post>(BundleKey.ReplyTarget.name)
    }

    private lateinit var binding: FragmentComposePostBinding

    private lateinit var adapter: ThumbnailAdapter

    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.ShowAccountList -> showAccountList()
        }
    }

    private fun showAccountList() {
        val fragment =
            ChangeAccountDialogFragment.newInstance(viewModel.currentUserIdLiveData.value.orEmpty())
        fragment.show(childFragmentManager, DialogKey.Accounts.name)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    @Inject
    lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase

    private val currentUserId: String by lazy {
        getCurrentAccountUseCase.run(Unit).account?.id.orEmpty()
    }

    private val mentionToMyself: Boolean by lazy {
        replyTarget != null && replyTarget?.user?.id == currentUserId
    }
    private val initialText by lazy {
        arguments?.getString(BundleKey.InitialText.name)
    }
    private val uriInfo by lazy {
        arguments?.getParcelableArrayList<UriInfo>(BundleKey.InitialPhoto.name)
    }

    val pollPostBody
        get() = pollFragment?.let {
            val composePollViewModel =
                ViewModelProvider(it)[ComposePollFragment.ComposePollViewModel::class.java]
            composePollViewModel.generatedPollPostBody
        }

    private fun send() {
        val text = viewModel.text.value ?: return
        val isNsfw = viewModel.nsfw.value ?: false
        val currentUserId = viewModel.currentUserIdLiveData.value ?: return
        val raw = mutableListOf<PostRaw<*>>()
        viewModel.longPost?.let { raw.add(it.copy(value = it.value.copy(tstamp = Date().time))) }
        viewModel.spoiler?.let { raw.add(it) }


        val postBodyOuter = PostBodyOuter(
            currentUserId,
            PostBody(text, replyTarget?.id, isNsfw = isNsfw, raw = raw.toList()),
            adapter.getItems(),
            pollPostBody
        )
        PostService.newPostIntent(context, postBodyOuter)
        listener?.onFinish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
        viewModel.counter.observe(this, counterObserver)
        viewModel.enablePoll.observe(this, enablePollObserver)
        uriInfo?.let { viewModel.media = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_compose_post, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
//        val shape = preferenceRepository.shapeOfAvatar
//        binding.myAccountAvatarImageView.setShape(shape)
//        binding.replyAvatarImageView.setShape(shape)
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.EditPhoto.ordinal -> {
                focusToEditText()
                if (resultCode != Activity.RESULT_OK || data == null) return
                updatePhoto(data)
            }
            RequestCode.Discard.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                cancelToCompose(true)
            }
            else -> {
                focusToEditText()
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun updatePhoto(data: Intent) {
        val editPhotoResult = EditPhotoActivity.parseIntent(data)
        val uriInfo = adapter.getItems()[editPhotoResult.index].copy(uri = editPhotoResult.uri)
        adapter.replace(uriInfo, editPhotoResult.index)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        syncMenuState()

        adapter = ThumbnailAdapter(viewModel.media.toMutableList(), thumbnailAdapterListener)
        viewModel.previewAttachmentsVisibility.value =
            if (adapter.getItems().isNotEmpty()) View.VISIBLE else View.GONE
        binding.thumbnailRecyclerView.adapter = adapter

        Util.setTintForToolbarIcons(
            binding.viewLeftActionMenuView.context,
            binding.viewLeftActionMenuView.menu
        )
        Util.setTintForToolbarIcons(
            binding.viewRightActionMenuView.context,
            binding.viewRightActionMenuView.menu
        )

        binding.viewLeftActionMenuView.setOnMenuItemClickListener(::onOptionsItemSelected)
        binding.viewRightActionMenuView.setOnMenuItemClickListener(::onOptionsItemSelected)

        replyTarget?.user?.let {
            GlideApp.with(this).load(it.content.avatarImage.link).dontAnimate()
                .into(binding.replyAvatarImageView)
        }

        viewModel.text.observeOnce(this, Observer<String> {
            binding.composeTextEditText.setSelection(it.length)
        })
        if (viewModel.initialized) {
            focusToEditText()
        } else {
            viewModel.initialized = true
        }
        val menu = binding.viewLeftActionMenuView.menu ?: return
        val longPostMenuItem = menu.findItem(R.id.menuLongPost) ?: return
        longPostMenuItem.isChecked = viewModel.longPost != null
        Util.setTintForCheckableMenuItem(view.context, longPostMenuItem)
    }

    private fun setupToolbar() {
        binding.toolbar.title =
            if (replyTarget != null)
                getString(R.string.compose_reply_title_template, replyTarget?.user?.username)
            else
                getString(R.string.compose_post)
        binding.toolbar.setOnMenuItemClickListener(::onOptionsItemSelected)
        binding.toolbar.setNavigationOnClickListener {
            cancelToCompose()
        }
    }

    private fun requestGalleryDialog() {
        val permission =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PermissionRequestCode.Storage.ordinal
            )
        } else if (permission == PackageManager.PERMISSION_GRANTED) {
            showGalleryDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionRequestCode.Storage.ordinal -> {
                if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGalleryDialog()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showGalleryDialog() {
        val fragment = GalleryItemListDialogFragment.chooseMultiple()
        fragment.show(childFragmentManager, DialogKey.Gallery.name)
    }

    override fun onGalleryItemClicked(uri: Uri, tag: String?) {
        adapter.add(UriInfo(uri))
        viewModel.previewAttachmentsVisibility.value = View.VISIBLE
    }


    override fun onShow() {
    }

    override fun onDismiss() {
        focusToEditText()
    }


    private fun focusToEditText() {
        binding.composeTextEditText.requestFocus()
        Util.showKeyboard(binding.composeTextEditText)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> cancelToCompose()
            R.id.menuInsertPhoto -> requestGalleryDialog()
            R.id.menuNsfw -> toggleNSFW(item)
            R.id.menuPost -> send()
            R.id.menuLongPost -> composeLongPost()
            R.id.menuSpoiler -> setSpoiler()
            R.id.menuPoll -> enablePoll()
        }
        return true
    }

    private fun enablePoll() {
        if (viewModel.enablePoll.value == true) return
        viewModel.enablePoll.value = true
    }

    private fun setSpoiler() {
        val spoiler = viewModel.spoiler
        val dialog = SpoilerDialogFragment.newInstance(spoiler)
        dialog.show(childFragmentManager, DialogKey.Spoiler.name)
    }

    private fun composeLongPost() {
        val fragment = ComposeLongPostFragment.newInstance(viewModel.longPost)
        listener?.addFragment(fragment)
    }

    private fun toggleNSFW(item: MenuItem) {
        val nextValue = !item.isChecked
        item.isChecked = nextValue
        Util.setTintForCheckableMenuItem(context!!, item)
        viewModel.nsfw.value = nextValue
    }


    private fun cancelToCompose(force: Boolean = false) {
        val hasAnyMedia = adapter.getItems().isNotEmpty()
        val hasAnyRaw =
            viewModel.longPost != null || viewModel.spoiler != null || pollPostBody != null
        val isChanged =
            viewModel.computedInitialText != viewModel.text.value || hasAnyMedia || hasAnyRaw
        if (!force && isChanged) {
            val fragment = BasicDialogFragment.Builder()
                .setMessage(R.string.discard_changes)
                .setPositive(R.string.discard)
                .build(RequestCode.Discard.ordinal)
            fragment.show(childFragmentManager, DialogKey.Discard.name)
        } else {
            listener?.onFinish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isVisible
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
//            focusToEditText()
        }
    }

    class ThumbnailAdapter(
        private val items: MutableList<UriInfo> = mutableListOf(),
        private val listener: Callback
    ) :
        RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {
        interface Callback {
            fun onRemove()
            fun onClick(uri: Uri, index: Int)
            fun updateList(list: List<UriInfo>)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.compose_thumbnail_image, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val uriInfo = items[position]
            GlideApp
                .with(holder.thumbnailView)
                .load(uriInfo.uri)
                .sizeMultiplier(.7f)
                .into(holder.thumbnailView)

            holder.removeButton.setOnClickListener { remove(holder.adapterPosition) }
            holder.thumbnailView.setOnClickListener { listener.onClick(uriInfo.uri, position) }
        }

        private fun remove(index: Int) {
            items.removeAt(index)
            listener.onRemove()
            listener.updateList(items)
            notifyItemRemoved(index)
        }

        fun addAll(uriList: List<UriInfo>) {
            items.addAll(uriList)
            listener.updateList(items)
            notifyItemRangeInserted(0, uriList.size)
        }

        fun add(uriInfo: UriInfo) {
            val index = items.size
            items.add(index, uriInfo)
            listener.updateList(items)
            notifyItemInserted(index)
        }

        fun replace(uriInfo: UriInfo, index: Int) {
            items[index] = uriInfo
            listener.updateList(items)
            notifyItemChanged(index)
        }

        fun getItems() = items

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumbnailView: ImageView = view.thumbnail
            val removeButton: ImageButton = view.removeButton
        }
    }

    sealed class Event {
        object ShowAccountList : Event()
    }


    class ComposePostViewModel private constructor(
        replyTargetArg: Post?,
        mentionToMyself: Boolean,
        initialText: String? = null,
        currentUserId: String
    ) : ViewModel() {
        val event = SingleLiveEvent<Event>()
        val currentUserIdLiveData: MutableLiveData<String> =
            MutableLiveData<String>().apply { value = currentUserId }
        val myAccountAvatarUrl: LiveData<String> =
            Transformations.map(currentUserIdLiveData) {
                User.getAvatarUrl(
                    it,
                    User.AvatarSize.Large
                )
            }
        var spoiler: Spoiler? = null
        var media: List<UriInfo> = emptyList()
        var initialized: Boolean = false
        val nsfw = MutableLiveData<Boolean>().apply { value = false }
        val replyTarget = MutableLiveData<Post>().apply { value = replyTargetArg }
        val replyTargetVisibility = Transformations.map(replyTarget) {
            if (it != null) View.VISIBLE else View.GONE
        }
        var longPost: LongPost? = null
        val text = MutableLiveData<String>().apply { value = "" }
        val counter: LiveData<Int> = Transformations.map(text) {
            val text = it ?: ""
            Constants.MaxPostTextLength - text.codePointCount(0, text.length)
        }
        val counterStr: LiveData<String> = Transformations.map(counter) { it.toString() }
        val previewAttachmentsVisibility = MutableLiveData<Int>().apply { value = View.GONE }
        val computedInitialText by lazy {
            val replyTargetUserUsername = replyTargetArg?.user?.username
            when {
                replyTargetUserUsername != null && !mentionToMyself -> "@$replyTargetUserUsername "
                initialText != null -> "$initialText "
                else -> ""
            }
        }
        var enablePoll = MutableLiveData<Boolean>().apply { value = false }

        init {
            text.value = computedInitialText
        }

        fun showAccountList() = event.emit((Event.ShowAccountList))

        class Factory(
            private val replyTarget: Post?,
            private val mentionToMyself: Boolean,
            private val initialText: String? = null,
            private val currentUserId: String
        ) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ComposePostViewModel(
                    replyTarget,
                    mentionToMyself,
                    initialText,
                    currentUserId
                ) as T
            }

        }
    }

    @Parcelize
    data class ComposePostFragmentOption(
        val initialText: String? = null,
        val intentExtraDataList: ArrayList<UriInfo>? = null
    ) : Parcelable

    companion object {

        fun newInstance(composePostFragmentOption: ComposePostFragmentOption? = null) =
            ComposePostFragment().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.InitialText.name, composePostFragmentOption?.initialText)
                    putParcelableArrayList(
                        BundleKey.InitialPhoto.name,
                        composePostFragmentOption?.intentExtraDataList
                    )
                }
            }

        fun replyInstance(post: Post) = ComposePostFragment().apply {
            arguments = Bundle().apply {
                putParcelable(BundleKey.ReplyTarget.name, post)
            }
        }
    }
}
