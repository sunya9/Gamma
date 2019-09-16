package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.domain.model.PollDeadline

@Parcelize
@JsonClass(generateAdapter = true)
data class PollPostBody(
    val prompt: String,
    val options: List<PollOption>,
    val duration: Int,
    @Json(name = "is_anonymous") val isAnonymous: Boolean,
    @Json(name = "max_options") val maxOptions: Int
) : Parcelable {
    @IgnoredOnParcel
    val type: String = BuildConfig.APPLICATION_ID
    @IgnoredOnParcel
    @Json(name = "is_public")
    val isPublic: Boolean = true
    @IgnoredOnParcel
    val pollDeadline = PollDeadline.fromInt(duration)

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class PollOption(
        val text: String = ""
    ) : Parcelable {
        companion object {
            val template
                get() = mutableListOf(PollOption(), PollOption())
        }
    }

    val edited: Boolean
        get() {
            val p = prompt.isNotEmpty()
            val d = duration != PollDeadline.defaultValue.toInt()
            val o = options.indexOfFirst { it.text.isNotEmpty() } >= 0
            val a = !isAnonymous
            val m = maxOptions != 1
            return p || d || o || a || m
        }


    companion object {
        const val MaxOptionSize = 10
        val defaultValue
            get() = PollPostBody(
                "",
                PollOption.template,
                PollDeadline.defaultValue.toInt(),
                true,
                1
            )
    }
}

