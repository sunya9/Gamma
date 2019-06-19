package net.unsweets.gamma.presentation.util

import android.content.Context
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_post_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.fragment.PostItemFragment


class PostTouchHelperCallback(
    val context: Context,
    val adapter: RecyclerView.Adapter<*>
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    private var prevIsCurrentlyActive: Boolean = false
    private var prevDX: Float = 0f
    private val deviceWidth: Int
    private var prevActionViewId: Int? = null

    init {
        val dm = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        deviceWidth = dm.widthPixels
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 1f
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 100000f
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 100000f
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val vh = (viewHolder as? PostItemFragment.PostViewHolder) ?: return
        val foregroundView = vh.itemView.postItemForegroundView
        val backgroundView = vh.itemView.swipeActionsLayout
        val per = dX / deviceWidth
        val direction = if (prevDX < dX) Direction.Right else Direction.Left
        if (isCurrentlyActive) {
            animActionViews(per, backgroundView, direction)
        }
        prevDX = dX
        ItemTouchHelper.Callback.getDefaultUIUtil()
            .onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
        val immediatelyAfterRelease = prevIsCurrentlyActive && !isCurrentlyActive
        if (immediatelyAfterRelease) {
            performClick(per, backgroundView)
        }
        prevIsCurrentlyActive = isCurrentlyActive
    }

    private fun performClick(per: Float, backgroundView: FrameLayout) {
        val viewId = getShownViewId(per, backgroundView) ?: return
        backgroundView.findViewById<View>(viewId)?.performClick()
    }


    private fun getShownViewId(per: Float, backgroundView: View): Int? {
        return when {
            per < 0.2 -> null
            0.2 <= per && per < 0.35 -> backgroundView.replyTextView.id
            0.35 <= per && per < 0.5 -> backgroundView.starTextView.id
            0.5 <= per && per < 0.65 -> backgroundView.repostTextView.id
            else -> backgroundView.moreImageView.id
        }
    }

    private fun animActionViews(
        per: Float,
        backgroundView: View,
        direction: Direction
    ) {
        val shownViewId = getShownViewId(per, backgroundView)
        if (prevActionViewId == shownViewId) return
        hideActionViewAnimation(prevActionViewId, backgroundView, direction)
        showActionViewAnimation(backgroundView, shownViewId, direction)
        prevActionViewId = shownViewId
    }

    private fun hideActionViewAnimation(
        prevActionViewId: Int?,
        backgroundView: View,
        direction: Direction
    ) {
        if (prevActionViewId == null) return
        val view = backgroundView.findViewById<View>(prevActionViewId)
        val scaleDownAnim =
            AnimationUtils.loadAnimation(context, R.anim.swipe_action_fade_out) as AnimationSet
        val translateAnim = getTranslateAnim(InOut.Out, direction)
        scaleDownAnim.addAnimation(translateAnim)
        view.startAnimation(scaleDownAnim)
        scaleDownAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                view.animation = null
                view.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }

    private fun showActionViewAnimation(
        backgroundView: View, @IdRes shownViewId: Int?,
        direction: Direction
    ) {
        if (shownViewId == null) return
        backgroundView.findViewById<View>(shownViewId)?.let {
            val scaleUpAnim = AnimationUtils.loadAnimation(context, R.anim.swipe_action_fade_in) as AnimationSet
            val translateAnim = getTranslateAnim(InOut.In, direction)
            scaleUpAnim.addAnimation(translateAnim)
            it.startAnimation(scaleUpAnim)
            it.visibility = View.VISIBLE
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = viewHolder.itemView.swipeActionsLayout
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
//        super.clearView(recyclerView, viewHolder)
    }

    sealed class Direction(val index: Int) {
        object Left : Direction(1)
        object Right : Direction(0)
    }

    sealed class InOut(val index: Int) {
        object In : InOut(0)
        object Out : InOut(1)
    }

    private val translateAnimations = listOf(
        listOf(R.anim.swipe_action_left_to_right_in, R.anim.swipe_action_right_to_left_in),
        listOf(R.anim.swipe_action_left_to_right_out, R.anim.swipe_action_right_to_left_out)
    )

    private fun getTranslateAnim(inOut: InOut, direction: Direction): Animation {
        val animRes = translateAnimations[inOut.index][direction.index]
        return AnimationUtils.loadAnimation(context, animRes)
    }
}