package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ChatSettings(override val value: ChatSettingsValue) : Raw<ChatSettings.ChatSettingsValue>, Parcelable {
    @IgnoredOnParcel
    override val type: String = ChatSettings.type

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ChatSettingsValue(
        val name: String,
        val description: String,
        val categories: List<Categories>?
    ) : Raw.RawValue, Parcelable {
        enum class Categories(val value: String) {
            @Json(name = "fun")
            FUN("fun"),
            @Json(name = "lifestyle")
            LIFESTYLE("lifestyle"),
            @Json(name = "profession")
            PROFESSION("profession"),
            @Json(name = "language")
            LANGUAGE("language"),
            @Json(name = "community")
            COMMUNITY("community"),
            @Json(name = "tech")
            TECH("tech"),
            @Json(name = "event")
            EVENT("event"),
            @Json(name = "general")
            GENERAL("general")
        }
    }

    companion object {
        fun getChatSettingsRaw(rawList: List<Raw<*>>): ChatSettings? = rawList.find { it is ChatSettings } as? ChatSettings
        const val type: String = "io.pnut.core.chat-settings"
    }
}