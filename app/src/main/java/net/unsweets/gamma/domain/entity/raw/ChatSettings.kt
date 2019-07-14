package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatSettings(override val value: ChatSettingsValue) : Raw<ChatSettings.ChatSettingsValue>, Parcelable {
    @IgnoredOnParcel
    override val type: String = "io.pnut.core.chat-settings"

    @Parcelize
    data class ChatSettingsValue(
        val name: String,
        val description: String,
        val categories: List<Categories>?
    ) : Raw.RawValue, Parcelable {
        enum class Categories(val value: String) {
            FUN("fun"),
            LIFESTYLE("lifestyle"),
            PROFESSION("profession"),
            LANGUAGE("language"),
            COMMUNITY("community"),
            TECH("tech"),
            EVENT("event"),
            GENERAL("general")
        }
    }
}