package net.unsweets.gamma.presentation.util

import android.view.View
import androidx.annotation.StringRes

data class SnackbarCallback(@StringRes val actionResId: Int, val callback: View.OnClickListener)