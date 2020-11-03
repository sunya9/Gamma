package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.transition.doOnEnd
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.fragment.ComposePostFragment
import net.unsweets.gamma.presentation.util.Util

class ComposePostActivity : BaseActivity(), ComposePostFragment.Callback {
    private val composePostFragment by lazy {
        ComposePostFragment.newInstance(composePostOption)
    }

    private val composePostOption by lazy {
        intent.getParcelableExtra<ComposePostFragment.ComposePostFragmentOption>(BundleKey.Option.name)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setupAnimation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_post)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.compose_post_placeholder, composePostFragment).commit()
        }
    }

    private fun setupAnimation() {
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        findViewById<View>(android.R.id.content).transitionName =
            getString(R.string.shared_element_compose)
        val primaryColor = ContextCompat.getColor(this, R.color.colorPrimary)
        val backgroundColor = ContextCompat.getColor(this, R.color.colorWindowBackground)
        val duration = resources.getInteger(R.integer.default_anim_duration).toLong()
        window.sharedElementEnterTransition = MaterialContainerTransform().also {
            it.fitMode = MaterialContainerTransform.FIT_MODE_AUTO
            it.pathMotion = MaterialArcMotion()
            it.interpolator = FastOutSlowInInterpolator()
            it.containerColor = primaryColor
            it.endContainerColor = backgroundColor
            it.addTarget(android.R.id.content)
            it.duration = duration
            it.doOnEnd {
                composePostFragment.focusToEditText()
            }
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().also {
            it.fitMode = MaterialContainerTransform.FIT_MODE_AUTO
            it.pathMotion = MaterialArcMotion()
            it.interpolator = FastOutSlowInInterpolator()
            it.containerColor = backgroundColor
            it.endContainerColor = primaryColor
            it.addTarget(android.R.id.content)
            it.duration = duration
        }
    }

    private enum class BundleKey {
        Option
    }

    companion object {
        fun newIntent(context: Context, option: ComposePostFragment.ComposePostFragmentOption? = null) = Intent(context, ComposePostActivity::class.java).also {
            it.putExtra(BundleKey.Option.name, option)
        }
    }

    override fun onBackPressed() {
        if(!composePostFragment.cancelToCompose()) return
        super.onBackPressed()
    }

    override fun onFinish() {
        Util.hideKeyboard(window.decorView, {
            // dirty hack
            // workaround for strange animation
            supportFinishAfterTransition()
        })
    }


    private val currentFragment
        get() = supportFragmentManager.findFragmentById(R.id.compose_post_placeholder)

    override fun addFragment(fragment: Fragment) {
        fragment.setTargetFragment(currentFragment, 1)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
            .replace(R.id.compose_post_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }
}