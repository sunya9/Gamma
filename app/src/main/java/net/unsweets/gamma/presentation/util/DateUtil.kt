package net.unsweets.gamma.presentation.util

import android.content.Context
import android.text.format.DateFormat
import net.unsweets.gamma.R
import java.util.*
import java.util.concurrent.TimeUnit


class DateUtil {
    companion object {
        @JvmStatic
        fun getShortDateStr(context: Context, date: Date?): String {
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
                seconds < 60 -> context.getString(R.string.date_short_seconds_template, seconds)
                minutes < 60 -> context.getString(R.string.date_short_minutes_template, minutes)
                hours < 24 -> context.getString(R.string.date_short_hours_template, hours)
                days == 1L -> context.getString(R.string.date_short_a_day)
                nowYear == dateYear -> {
                    val dateFormatTemplate = context.getString(R.string.date_short_date_this_year)
                    String.format(Locale.US, dateFormatTemplate, dateCal, dateCal, dateCal)
                }
                else -> formatAbsoluteShortDateStr(context, date)
            }
        }

        private fun formatAbsoluteShortDateStr(context: Context, date: Date?): String {
            val dateCal = Calendar.getInstance().apply {
                time = date
            }
            val dateFormatTemplate = context.getString(R.string.date_short_date_before_last_year)
            return String.format(Locale.US, dateFormatTemplate, dateCal, dateCal, dateCal)
        }

        @JvmStatic
        fun getShortDateStrWithTime(context: Context, date: Date?): String {
            if (date == null) return ""
            val dateCal = Calendar.getInstance().apply {
                time = date
            }
            val absoluteShortDateStr = formatAbsoluteShortDateStr(context, date)
            val timeFormatTemplate = context.getString(R.string.format_time)
            val time = String.format(Locale.US, timeFormatTemplate, dateCal, dateCal, dateCal)
            return "$absoluteShortDateStr $time"
        }
    }
}

fun Date.toFormatString(context: Context?): String {
    val dateFormatTemplate = context?.getString(R.string.file_date_format_template)
    return DateFormat.format(dateFormatTemplate, this).toString()
}