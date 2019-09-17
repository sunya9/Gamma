package net.unsweets.gamma.domain.entity

import android.content.Context
import net.unsweets.gamma.R
import net.unsweets.gamma.util.toFormatString
import java.util.*

interface PollLikeValue {
    val prompt: String
    val options: List<Poll.PollOption>
    val pollToken: String
    val closedAt: Date
    val id: String
    val alreadyClosed
        get() = closedAt.time < Date().time

    fun getDateText(context: Context): String {
        val dateStr = closedAt.toFormatString(context)
        val templateRes = when {
            alreadyClosed -> R.string.poll_closed_date_template
            else -> R.string.poll_held_date_template
        }
        return context.getString(templateRes, dateStr)
    }
}