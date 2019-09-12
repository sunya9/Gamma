package net.unsweets.gamma.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import net.unsweets.gamma.R
import kotlin.math.round


class SeekBarPreferenceMod @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SeekBarPreference(context, attrs), SeekBar.OnSeekBarChangeListener {
    private val step: Int

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SeekBarPreference, 0, 0
        )
        step = typedArray.getInt(
            androidx.preference.R.styleable.SeekBarPreference_seekBarIncrement,
            20
        )
        typedArray.recycle()

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val newProgress = round(progress.toFloat() / step.toFloat()).toInt() * step + min
        value = newProgress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onBindViewHolder(view: PreferenceViewHolder?) {
        super.onBindViewHolder(view)
        if (view == null) return
        val seekBar = view.findViewById(androidx.preference.R.id.seekbar) as? SeekBar ?: return
        seekBar.setOnSeekBarChangeListener(this)
    }
}