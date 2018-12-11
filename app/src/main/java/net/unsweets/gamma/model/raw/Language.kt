package net.unsweets.gamma.model.raw

data class Language(override val value: LanguageValue) : Raw.IRaw {
    data class LanguageValue(val language: String) : Raw.RawValue

    override val type = "io.pnut.core.language"
}