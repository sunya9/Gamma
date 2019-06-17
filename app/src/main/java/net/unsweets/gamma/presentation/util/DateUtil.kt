package net.unsweets.gamma.presentation.util

import android.content.Context
import android.text.format.DateFormat
import net.unsweets.gamma.R
import java.util.*
import java.util.concurrent.TimeUnit


class DateUtil {
    companion object {
        fun getShortDateStr(date: Date?): String {
            if (date == null) return ""
            val now = Date()
            val duration = now.time - date.time
            val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
            val hours = TimeUnit.MILLISECONDS.toHours(duration)
            val days = TimeUnit.MILLISECONDS.toDays(duration)
            val nowCal = Calendar.getInstance().apply {
                time = now
            }
            val dateCal = Calendar.getInstance().apply {
                time = date
            }
            val nowYear = nowCal.get(Calendar.YEAR)
            val dateYear = dateCal.get(Calendar.YEAR)
            return when {
                seconds < 60 -> "${seconds}s"
                minutes < 60 -> "${minutes}m"
                hours < 24 -> "${hours}h"
                days == 1L -> "a day"
                nowYear == dateYear -> String.format(Locale.US, "%te %tb", dateCal, dateCal, dateCal)
                else -> String.format(Locale.US, "%te %tb %ty", dateCal, dateCal, dateCal)
            }
        }
    }
}

fun Date.toFormatString(context: Context?): String {
    val dateFormatTemplate = context?.getString(R.string.file_date_format_template)
    return DateFormat.format(dateFormatTemplate, this).toString()
}