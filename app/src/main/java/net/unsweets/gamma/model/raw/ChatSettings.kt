package net.unsweets.gamma.model.raw

data class ChatSettings(override val value: ChatSettingsValue) : Raw.IRaw {
    override val type: String = "io.pnut.core.chat-settings"

    data class ChatSettingsValue(
        val name: String,
        val description: String,
        val categories: List<Categories>?
    ) : Raw.RawValue {
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