package net.unsweets.gamma.presentation.fragment


import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import kotlinx.android.synthetic.main.compose_thumbnail_image.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentComposePostBinding
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.entity.PostBodyOuter
import net.unsweets.gamma.domain.entity.raw.LongPost
import net.unsweets.gamma.domain.entity.raw.PostRaw
import net.unsweets.gamma.domain.usecases.GetCurrentAccountUseCase
import net.unsweets.gamma.presentation.activity.EditPhotoActivity
import net.unsweets.gamma.presentation.util.*
import net.unsweets.gamma.presentation.viewmodel.BaseViewModel
import net.unsweets.gamma.service.PostService
import net.unsweets.gamma.util.Constants
import net.unsweets.gamma.util.observeOnce
import java.util.*
import javax.inject.Inject

class ComposePostFragment : BaseFragment(), GalleryItemListDialogFragment.Listener, AnimationCallback,
    BackPressedHookable, ComposeLongPostFragment.Callback {
    override fun onUpdateLongPost(longPost: LongPost?) {
        viewModel.longPost = longPost
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
            hideKeyboard(binding.composeTextEditText)
        }
    }

    interface Callback {
        fun onFinish()
        fun addFragment(fragment: Fragment)
    }

    private enum class BundleKey {
        ReplyTarget
    }

    private enum class DialogKey {
        Gallery, Discard
    }

    private enum class PermissionRequestCode {
        Storage
    }


    private enum class RequestCode { EditPhoto, Discard }

    private var listener: Callback? = null
    private val thumbnailAdapterListener = object : ThumbnailAdapter.Callback {
        override fun updateList(list: List<Uri>) {
            viewModel.photos = list.toMutableList()
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
        setTintForCheckableMenuItem(context!!, nsfwMenuItem)
    }


    private fun syncMenuState() {
        updateSendMenuItem()
        updateNsfwMenuItem()
    }

    private val viewModel: ComposePostViewModel by lazy {
        ViewModelProviders.of(
            this,
            ComposePostViewModel.Factory(activity!!.application, replyTarget, mentionToMyself)
        )
            .get(ComposePostViewModel::class.java)
    }

    private val replyTarget: Post? by lazy {
        arguments?.getParcelable<Post>(BundleKey.ReplyTarget.name)
    }

    private lateinit var binding: FragmentComposePostBinding

    private lateinit var adapter: ThumbnailAdapter

    private val eventObserver = Observer<Event> {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    @Inject
    lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase

    private val currentUserId: String by lazy {
        getCurrentAccountUseCase.run(Unit).account?.id ?: ""
    }

    private val mentionToMyself: Boolean by lazy {
        replyTarget != null && replyTarget?.user?.id == currentUserId
    }

    private fun send() {
        val text = viewModel.text.value ?: return
        val isNsfw = viewModel.nsfw.value ?: false
        val raw = mutableListOf<PostRaw<*>>()
        viewModel.longPost?.let { raw.add(it.copy(value = it.value.copy(tstamp = Date().time))) }
        val postBodyOuter = PostBodyOuter(
            PostBody(text, replyTarget?.id, isNsfw = isNsfw, raw = raw.toList()),
            adapter.getItems()
        )
        PostService.newPostIntent(context, postBodyOuter)
        listener?.onFinish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
        viewModel.counter.observe(this, counterObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_post, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
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
        adapter.replace(editPhotoResult.uri, editPhotoResult.index)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        syncMenuState()

        adapter = ThumbnailAdapter(viewModel.photos.toMutableList(), thumbnailAdapterListener)
        binding.thumbnailRecyclerView.adapter = adapter

        setTintForToolbarIcons(binding.viewLeftActionMenuView.context, binding.viewLeftActionMenuView.menu)

        binding.viewLeftActionMenuView.setOnMenuItemClickListener(::onOptionsItemSelected)
        binding.viewRightActionMenuView.setOnMenuItemClickListener(::onOptionsItemSelected)

        replyTarget?.user?.let {
            GlideApp.with(this).load(it.content.avatarImage.link).into(binding.replyAvatarImageView)
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
        setTintForCheckableMenuItem(view.context, longPostMenuItem)
    }

    private fun setupToolbar() {
        binding.toolbar.title =
            if (replyTarget != null)
                getString(R.string.compose_reply_title_template, replyTarget?.user?.username)
            else
                getString(R.string.compose_a_post)
        binding.toolbar.setOnMenuItemClickListener(::onOptionsItemSelected)
        binding.toolbar.setNavigationOnClickListener {
            cancelToCompose()
        }
    }

    private fun requestGalleryDialog() {
        val permission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
        adapter.add(uri)
        viewModel.previewAttachmentsVisibility.value = View.VISIBLE
    }


    override fun onShow() {
    }

    override fun onDismiss() {
        focusToEditText()
    }


    private fun focusToEditText() {
        binding.composeTextEditText.requestFocus()
        showKeyboard(binding.composeTextEditText)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> cancelToCompose()
            R.id.menuInsertPhoto -> requestGalleryDialog()
            R.id.menuNsfw -> toggleNSFW(item)
            R.id.menuPost -> send()
            R.id.menuLongPost -> composeLongPost()
        }
        return true
    }

    private fun composeLongPost() {
        val fragment = ComposeLongPostFragment.newInstance(viewModel.longPost)
        listener?.addFragment(fragment)
    }

    private fun toggleNSFW(item: MenuItem) {
        val nextValue = !item.isChecked
        item.isChecked = nextValue
        setTintForCheckableMenuItem(context!!, item)
        viewModel.nsfw.value = nextValue
    }


    private fun cancelToCompose(force: Boolean = false) {
        val isChanged = viewModel.initialText != viewModel.text.value || adapter.getItems().isNotEmpty()
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

    class ThumbnailAdapter(private val items: MutableList<Uri> = mutableListOf(), private val listener: Callback) :
        RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {
        interface Callback {
            fun onRemove()
            fun onClick(uri: Uri, index: Int)
            fun updateList(list: List<Uri>)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.compose_thumbnail_image, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val url = items[position]
            GlideApp
                .with(holder.thumbnailView)
                .load(url)
                .sizeMultiplier(.7f)
                .into(holder.thumbnailView)

            holder.removeButton.setOnClickListener { remove(holder.adapterPosition) }
            holder.thumbnailView.setOnClickListener { listener.onClick(url, position) }
        }

        private fun remove(index: Int) {
            items.removeAt(index)
            listener.onRemove()
            listener.updateList(items)
            notifyItemRemoved(index)
        }

        fun add(uri: Uri) {
            val index = items.size
            items.add(index, uri)
            listener.updateList(items)
            notifyItemInserted(index)
        }

        fun replace(uri: Uri, index: Int) {
            items[index] = uri
            listener.updateList(items)
            notifyItemChanged(index)
        }

        fun getItems() = items

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumbnailView: ImageView = view.thumbnail
            val removeButton: ImageButton = view.removeButton
        }
    }


    class ComposePostViewModel(
        app: Application,
        replyTargetArg: Post?,
        mentionToMyself: Boolean
    ) : BaseViewModel<Event>(app) {
        var photos: List<Uri> = emptyList()
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
        val initialText by lazy {
            val replyTargetUserUsername = replyTargetArg?.user?.username
            if (replyTargetUserUsername != null && !mentionToMyself)
                "@$replyTargetUserUsername "
            else
                ""
        }

        init {
            text.value = initialText
        }

        class Factory(
            private val app: Application,
            private val replyTarget: Post?,
            private val mentionToMyself: Boolean
        ) :
            ViewModelProvider.AndroidViewModelFactory(app) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ComposePostViewModel(app, replyTarget, mentionToMyself) as T
            }

        }
    }

    sealed class Event {
        data class Send(val postBody: PostBody) : Event()
    }

    companion object {
        fun newInstance() = ComposePostFragment()

        fun replyInstance(post: Post) = ComposePostFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ComposePostFragment.BundleKey.ReplyTarget.name, post)
            }
        }
    }
}
