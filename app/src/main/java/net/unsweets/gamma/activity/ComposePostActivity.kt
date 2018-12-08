package net.unsweets.gamma.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import net.unsweets.gamma.R
import net.unsweets.gamma.api.model.PostBody
import net.unsweets.gamma.databinding.ActivityComposePostBinding
import net.unsweets.gamma.model.Post
import net.unsweets.gamma.util.Store
import net.unsweets.gamma.util.then
import android.view.ViewTreeObserver
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.unsweets.gamma.fragment.GalleryItemListDialogFragment
import net.unsweets.gamma.util.showKeyboard


class ComposePostActivity : BaseActivity(), GalleryItemListDialogFragment.Listener {
    private enum class IntentKey {
        CX, CY
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
    }

    private lateinit var viewModel: ComposePostViewModel

    private lateinit var binding: ActivityComposePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compose_post)
        if(savedInstanceState == null) revealAnimation(binding.rootLayout)
        viewModel = ViewModelProviders.of(this).get(ComposePostViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setTitle(R.string.compose_a_post)
        }
        viewModel.event.observe(this, Observer {
            when (it) {
                Event.SendPost -> sendPost()
                Event.AttachPicture -> requestGalleryDialog()
                else -> Unit
            }
        })
    }

    private fun requestGalleryDialog() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionRequestCode.Storage.ordinal)
        } else if(permission == PackageManager.PERMISSION_GRANTED) {
            showGalleryDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PermissionRequestCode.Storage.ordinal -> {
                if(grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGalleryDialog()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showGalleryDialog() {
        val fragment = GalleryItemListDialogFragment.newInstance()
        fragment.show(supportFragmentManager, FragmentKey.Gallery.name)
    }

    override fun onGalleryItemClicked(position: String) {

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
        if(intent == null) return null
        val root = binding.root
        val cx = intent.getIntExtra(IntentKey.CX.name, -1)
        val cy = intent.getIntExtra(IntentKey.CY.name, -1)
        if((cx < 0) or (cy < 0)) return null
        val targetRadius = Math.hypot(root.width.toDouble(), root.height.toDouble()).toFloat()
        val startRadius = if(open) 0F else targetRadius
        val endRadius = if(open) targetRadius else 0F
        val anim = ViewAnimationUtils.createCircularReveal(root, cx, cy, startRadius, endRadius)
        anim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if(!open) return
                binding.composeTextEditText.requestFocus()
                showKeyboard(this@ComposePostActivity)
            }
        })
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        anim.duration = duration.toLong()
        return anim
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitReveal()
    }

    // Flickering...
    // private fun exitReveal() = createRevealAnim(false)?.start()
    private fun exitReveal() = Unit


    private fun sendPost() {
        val text = viewModel.text.value ?: return
        pnut.createPost(PostBody(text)).then {
        }
        finishWithAnim()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                cancelToCompose()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cancelToCompose() {
        // TODO: confirm to really finish or not
        finishWithAnim()
    }

    private fun finishWithAnim() {
        exitReveal()
        finish()
    }


    class ComposePostViewModel : Store<Event>() {
        val replyTarget = MutableLiveData<Post>()
        val text = MutableLiveData<String>().apply { value = "" }
        private val counter: LiveData<Int> = Transformations.map(text) {
            val text = it ?: ""
            256 - text.codePointCount(0, text.length)
        }
        val counterStr: LiveData<String> = Transformations.map(counter) { it.toString() }
        val enableSendButton: LiveData<Boolean> = Transformations.map(counter) { (0 < (it ?: 0)) and !TextUtils.isEmpty(text.value ?: "") }
        fun sendPost() = sendEvent(Event.SendPost)
        fun attachPicture() = sendEvent(Event.AttachPicture)
    }

    enum class Event {
        SendPost,
        AttachPicture
    }
}
