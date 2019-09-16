package net.unsweets.gamma.presentation.fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_compose_post_dialog.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.presentation.util.AnimationCallback
import net.unsweets.gamma.presentation.util.BackPressedHookable
import net.unsweets.gamma.presentation.util.ThemeColorUtil
import net.unsweets.gamma.util.LogUtil
import kotlin.math.hypot

class ComposePostDialogFragment : DialogFragment(), ComposePostFragment.Callback {

    override fun addFragment(fragment: Fragment) {
        fragment.setTargetFragment(currentFragment, 1)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
            .replace(R.id.rootLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onFinish() {
        finishWithAnim()
    }

    private enum class BundleKey {
        CX, CY, ReplyTarget, ComposePostFragmentOption
    }

    private var listener: Callback? = null

    interface Callback {
        fun onDismiss()
    }
    private val replyTarget: Post? by lazy {
        arguments?.getParcelable<Post>(BundleKey.ReplyTarget.name)
    }
    private val composePostFragmentOption by lazy {
        arguments?.getParcelable<ComposePostFragment.ComposePostFragmentOption>(BundleKey.ComposePostFragmentOption.name)
    }

    private val composePostFragment by lazy {
        val rt = replyTarget
        when {
            rt != null -> ComposePostFragment.replyInstance(rt)
            else -> ComposePostFragment.newInstance(composePostFragmentOption)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeColorUtil.applyTheme(this)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_compose_post_dialog, container, false)
    }

    private val currentFragment
        get() = childFragmentManager.findFragmentById(R.id.rootLayout)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.rootLayout, composePostFragment)
                .commit()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnDismissListener(this)
        dialog.setOnKeyListener { _, keyCode, event ->
            val fm = childFragmentManager
            val fragment = currentFragment
            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                when {
                    fragment is BackPressedHookable -> fragment.onBackPressed()
                    fm.backStackEntryCount > 0 -> fm.popBackStack()
                    else -> exitReveal()
                }
                true
            } else {
                false
            }
        }
        dialog.window?.decorView?.visibility = View.GONE
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            if (savedInstanceState == null) {
                revealAnimation(dialog.rootLayout)
                view.let {
                    val anim = AnimatorInflater.loadAnimator(context, R.animator.bg_compose_window)
                    anim.setTarget(dialog.window)
                    anim.start()
                }
            }
        }


        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun createRevealAnim(open: Boolean = true, root: View): Animator? {
        LogUtil.e("exitReveal")
        val args = arguments ?: return null
        val cx = args.getInt(BundleKey.CX.name, -1)
        val cy = args.getInt(BundleKey.CY.name, -1)
        if ((cx < 0) || (cy < 0)) return null
        val targetRadius = hypot(root.width.toDouble(), root.height.toDouble()).toFloat()
        val startRadius = if (open) 0F else targetRadius
        val endRadius = if (open) targetRadius else 0F
        val anim = ViewAnimationUtils.createCircularReveal(root, cx, cy, startRadius, endRadius)
        anim.interpolator = AccelerateInterpolator()
        val fragment = currentFragment as? AnimationCallback
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                fragment?.onAnimationStart(open)
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (!open) {
                    root.visibility = View.INVISIBLE
                    dismiss()
                    return
                }
                fragment?.onAnimationEnd(open)
            }
        })
        val duration = resources.getInteger(R.integer.compose_duration)
        anim.duration = duration.toLong()
        return anim
    }

    private fun exitReveal() = createRevealAnim(false, rootLayout)?.start() ?: dismiss()
    private fun finishWithAnim() {
        LogUtil.e("finishWithAnim")
        exitReveal()
    }

    private fun revealAnimation(root: View) {
        val viewTreeObserver = root.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    dialog?.window?.decorView?.visibility = View.VISIBLE
                    createRevealAnim(true, view!!)?.start()
                }
            })
        }
    }

    fun popBackStack() {
        childFragmentManager.popBackStack()
    }

    companion object {
        fun newInstance(
            cx: Int,
            cy: Int,
            composePostFragmentOption: ComposePostFragment.ComposePostFragmentOption? = null
        ) = ComposePostDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(BundleKey.CX.name, cx)
                putInt(BundleKey.CY.name, cy)
                putParcelable(BundleKey.ComposePostFragmentOption.name, composePostFragmentOption)
            }
        }

        fun replyInstance(cx: Int, cy: Int, post: Post) = ComposePostDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(BundleKey.CX.name, cx)
                putInt(BundleKey.CY.name, cy)
                putParcelable(BundleKey.ReplyTarget.name, post)
            }
        }
    }

}
