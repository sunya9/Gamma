package net.unsweets.gamma.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout


class WindowInsetsFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {

        // Look for replaced fragments and apply the insets again.
        setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                requestApplyInsets()
            }

            override fun onChildViewRemoved(parent: View, child: View) {

            }
        })
    }

}