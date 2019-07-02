package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(override val value: LanguageValue) : Raw<Language.LanguageValue>(), Parcelable {
    @Parcelize
    data class LanguageValue(val language: String) : RawValue, Parcelable

    @IgnoredOnParcel
    override val type = "io.pnut.core.language"
}