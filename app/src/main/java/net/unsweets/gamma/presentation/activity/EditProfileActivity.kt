package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.transition.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.fragment.EditProfileFragment

class EditProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setupAnimation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        replaceFragment(savedInstanceState == null)
    }

    private val userId by lazy {
        intent.getStringExtra(BundleKey.UserId.name)
    }

    private fun setupAnimation() {
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        findViewById<View>(android.R.id.content).transitionName =
            getString(R.string.shared_element_edit_profile)
        val duration = resources.getInteger(R.integer.default_anim_duration).toLong()
        window.sharedElementEnterTransition = MaterialContainerTransform().also {
            it.fitMode = MaterialContainerTransform.FIT_MODE_AUTO
            it.pathMotion = MaterialArcMotion()
            it.interpolator = FastOutSlowInInterpolator()
            it.isElevationShadowEnabled = false
            it.startShapeAppearanceModel =
                ShapeAppearanceModel.builder().setAllCornerSizes(ShapeAppearanceModel.PILL).build()
            it.endShapeAppearanceModel =
                ShapeAppearanceModel.builder().setAllCornerSizes(0f).build()
            it.addTarget(android.R.id.content)
            it.duration = duration
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().also {
            it.fitMode = MaterialContainerTransform.FIT_MODE_AUTO
            it.pathMotion = MaterialArcMotion()
            it.interpolator = FastOutSlowInInterpolator()
            it.isElevationShadowEnabled = false
            it.startShapeAppearanceModel =
                ShapeAppearanceModel.builder().setAllCornerSizes(0f).build()
            it.endShapeAppearanceModel =
                ShapeAppearanceModel.builder().setAllCornerSizes(ShapeAppearanceModel.PILL).build()
            it.addTarget(android.R.id.content)
            it.duration = duration
        }
    }

    private fun replaceFragment(firstTime: Boolean) {
        if (!firstTime) return
        val fragment = EditProfileFragment.newInstance(userId)
        supportFragmentManager.beginTransaction().replace(R.id.edit_profile_placeholder, fragment)
            .commit()
    }

    private enum class BundleKey {
        UserId
    }

    companion object {
        fun newIntent(context: Context, userId: String) =
            Intent(context, EditProfileActivity::class.java).also {
                it.putExtra(BundleKey.UserId.name, userId)
            }
    }
}