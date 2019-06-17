package net.unsweets.gamma.presentation.fragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.activity_compose_post.*
import kotlinx.android.synthetic.main.activity_compose_post.view.*
import kotlinx.android.synthetic.main.compose_thumbnail_image.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.ActivityComposePostBinding
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.presentation.activity.ComposePostActivity
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.presentation.util.hideKeyboard
import net.unsweets.gamma.presentation.util.showKeyboard
import net.unsweets.gamma.presentation.viewmodel.BaseViewModel

class ComposePostFragment : DaggerAppCompatDialogFragment(), GalleryItemListDialogFragment.Listener {
    private enum class BundleKey {
        CX, CY, Text
    }

    private enum class FragmentKey {
        Gallery
    }

    private enum class PermissionRequestCode {
        Storage
    }

    companion object {
        fun newInstance(cx: Int, cy: Int) = ComposePostFragment().apply {
            arguments = Bundle().apply {
                putInt(BundleKey.CX.name, cx)
                putInt(BundleKey.CY.name, cy)
            }
        }

        fun shareUrlIntent(context: Context, title: String?, link: String) =
            Intent(context, ComposePostActivity::class.java).apply {
                val text = context.getString(R.string.markdown_link, title, link)
                putExtra(BundleKey.Text.name, text)
            }
    }

    private val viewModel: ComposePostViewModel by lazy {
        ViewModelProviders.of(this).get(ComposePostViewModel::class.java)
    }

    private lateinit var binding: ActivityComposePostBinding

    private lateinit var adapter: ThumbnailAdapter

    private val eventObserver = Observer<Event> {
        when (it) {
            Event.Sent -> sent()
            Event.AttachPicture -> requestGalleryDialog()
            else -> Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_compose_post, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setTitle(R.string.compose_a_post)
        binding.toolbar.setOnMenuItemClickListener(::onOptionsItemSelected)
        binding.toolbar.setNavigationOnClickListener {
            cancelToCompose()
        }
        viewModel.event.observe(this, eventObserver)
        view.composeTextEditText.setOnFocusChangeListener { editText, b ->
            if(!b) return@setOnFocusChangeListener
            showKeyboard(editText)
        }
        adapter = ThumbnailAdapter()
        thumbnailRecyclerView.adapter = adapter

        binding.viewLeftActionMenuView.setOnMenuItemClickListener(::onOptionsItemSelected)
        binding.viewRightActionMenuView.setOnMenuItemClickListener(::onOptionsItemSelected)
    }
    private fun sent() {
        finishWithAnim()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            if(savedInstanceState == null) {
                revealAnimation(dialog.rootLayout)

            } else {
                focusToEditText()
            }
        }
        dialog.setOnKeyListener { _, keyCode, _ ->
            if(keyCode != KeyEvent.KEYCODE_BACK) return@setOnKeyListener false
                exitReveal()
            true
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
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
        hideKeyboard(binding.composeTextEditText)
        val fragment = GalleryItemListDialogFragment.newInstance()
        fragment.show(childFragmentManager, FragmentKey.Gallery.name)
    }

    override fun onGalleryItemClicked(path: String) {
        adapter.add(path)
    }


    override fun onShow() {
        hideKeyboard(binding.composeTextEditText)
    }

    override fun onDismiss() {
        showKeyboard(binding.composeTextEditText)
        binding.composeTextEditText.apply {
            requestFocus()
            visibility = View.INVISIBLE
            visibility = View.VISIBLE
            requestFocus()
        }
    }

    private fun revealAnimation(root: View) {
        val viewTreeObserver = root.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    createRevealAnim(true, view!!)?.start()
                }
            })
        }
    }


    fun createRevealAnim(open: Boolean = true, root: View): Animator? {
        val args = arguments ?: return null
        val cx = args.getInt(BundleKey.CX.name, -1)
        val cy = args.getInt(BundleKey.CY.name, -1)
        if ((cx < 0) || (cy < 0)) return null
        val targetRadius = Math.hypot(root.width.toDouble(), root.height.toDouble()).toFloat()
        val startRadius = if (open) 0F else targetRadius
        val endRadius = if (open) targetRadius else 0F
        val anim = ViewAnimationUtils.createCircularReveal(root, cx, cy, startRadius, endRadius)
        anim.interpolator = AccelerateInterpolator()
        if (!open) hideKeyboard(binding.composeTextEditText)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (!open) {
                    root.visibility = View.INVISIBLE
                    dismiss()
                    return
                } else {
                    focusToEditText()
                }
            }
        })
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        anim.duration = duration.toLong()
        return anim
    }

    private fun focusToEditText() {
//        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.composeTextEditText.requestFocus()
        showKeyboard(binding.composeTextEditText)
    }

    private fun exitReveal() = createRevealAnim(false, binding.root)?.start() ?: dismiss()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> cancelToCompose()
            R.id.menuInsertPhoto -> requestGalleryDialog()
        }
        return true
    }

    private fun cancelToCompose() {
        // TODO: confirm to really finish or not
        finishWithAnim()
    }

    private fun finishWithAnim() {
        exitReveal()
    }

    class ThumbnailAdapter : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {
        private val items = ArrayList<String>()

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
            holder.thumbnailView.setOnClickListener { }
        }

        fun remove(index: Int) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }

        fun add(path: String) {
            val index = items.size
            items.add(index, path)
            notifyItemInserted(index)
        }

        fun getItems() = items

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumbnailView: ImageView = view.thumbnail
            val removeButton: ImageButton = view.removeButton
        }
    }


    class ComposePostViewModel(app: Application) : BaseViewModel<Event>(app) {
        val replyTarget = MutableLiveData<Post>()
        val text = MutableLiveData<String>().apply { value = "" }
        private val counter: LiveData<Int> = Transformations.map(text) {
            val text = it ?: ""
            256 - text.codePointCount(0, text.length)
        }
        val counterStr: LiveData<String> = Transformations.map(counter) { it.toString() }
        val enableSendButton: LiveData<Boolean> =
            Transformations.map(counter) { (0 < (it ?: 0)) and !TextUtils.isEmpty(text.value ?: "") }

        fun sendPost() {
            val text = text.value ?: return
//            val result = pnut.createPost(PostBody(text)).toLiveData()
            sendEvent(Event.Sent)
//            when(result.value) {
//                is Promise.Success -> sendEvent(Event.Sent)
//            }
        }

        fun attachPicture() = sendEvent(Event.AttachPicture)
    }

    enum class Event {
        AttachPicture, Sent
    }
}
