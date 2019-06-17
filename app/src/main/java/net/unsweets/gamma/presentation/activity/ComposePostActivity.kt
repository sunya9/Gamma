package net.unsweets.gamma.presentation.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_compose_post.*
import kotlinx.android.synthetic.main.compose_thumbnail_image.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.ActivityComposePostBinding
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.presentation.fragment.GalleryItemListDialogFragment
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.presentation.util.hideKeyboard
import net.unsweets.gamma.presentation.util.showKeyboard
import net.unsweets.gamma.presentation.viewmodel.BaseViewModel


class ComposePostActivity : BaseActivity(), GalleryItemListDialogFragment.Listener {
    private enum class IntentKey {
        CX, CY, Text
    }

    private enum class FragmentKey {
        Gallery
    }

    private enum class PermissionRequestCode {
        Storage
    }

    companion object {
        fun newIntent(context: Context, cx: Int, cy: Int) = Intent(context, ComposePostActivity::class.java).apply {
            putExtra(IntentKey.CX.name, cx)
            putExtra(IntentKey.CY.name, cy)
        }

        fun shareUrlIntent(context: Context, title: String?, link: String) =
            Intent(context, ComposePostActivity::class.java).apply {
                val text = context.getString(R.string.markdown_link, title, link)
                putExtra(IntentKey.Text.name, text)
            }
    }

    private lateinit var viewModel: ComposePostViewModel

    private lateinit var binding: ActivityComposePostBinding

    private lateinit var adapter: ThumbnailAdapter

    private val eventObserver = Observer<Event> {
        when (it) {
            Event.Sent -> sent()
            Event.AttachPicture -> requestGalleryDialog()
            else -> Unit
        }
    }
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compose_post)
        if (savedInstanceState == null) revealAnimation(binding.rootLayout)
        viewModel = ViewModelProviders.of(this).get(ComposePostViewModel::class.java)
        binding.lifecycleOwner = this
//        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            //            it.setDisplayHomeAsUpEnabled(true)
            it.setTitle(R.string.compose_a_post)
        }
        viewModel.event.observe(this, eventObserver)
        binding.composeTextEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) return@setOnFocusChangeListener
            showKeyboard(v)
        }
        adapter = ThumbnailAdapter()
        thumbnailRecyclerView.adapter = adapter


        menuInflater.inflate(R.menu.compose_left, binding.viewLeftActionMenuView.menu)
        binding.viewLeftActionMenuView.setOnMenuItemClickListener { onOptionsItemSelected(it) }


        menuInflater.inflate(R.menu.compose_right, binding.viewRightActionMenuView.menu)
        binding.viewRightActionMenuView.setOnMenuItemClickListener { onOptionsItemSelected(it) }
    }

    private fun sent() {
        finishWithAnim()
    }

    private fun requestGalleryDialog() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
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
        fragment.show(supportFragmentManager, FragmentKey.Gallery.name)
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
                    createRevealAnim()?.start()
                }
            })
        }
    }

    fun createRevealAnim(open: Boolean = true): Animator? {
        if (intent == null) return null
        val root = binding.root
        val cx = intent.getIntExtra(IntentKey.CX.name, -1)
        val cy = intent.getIntExtra(IntentKey.CY.name, -1)
        if ((cx < 0) or (cy < 0)) return null
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
                    finish()
                    overridePendingTransition(0, 0)
                    return
                }
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                binding.composeTextEditText.requestFocus()
                showKeyboard(binding.composeTextEditText)
            }
        })
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        anim.duration = duration.toLong()
        return anim
    }

    override fun onBackPressed() {
        exitReveal()
    }

    private fun exitReveal() = createRevealAnim(false)?.start() ?: finish()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.let {
            when (it.itemId) {
                android.R.id.home -> cancelToCompose()
                R.id.menuInsertPhoto -> requestGalleryDialog()
            }
            return true
        } ?: super.onOptionsItemSelected(item)
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
