package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(override val value: LanguageValue) : Raw.IRaw, Parcelable {
    @Parcelize
    data class LanguageValue(val language: String) : Raw.RawValue, Parcelable

    override val type = "io.pnut.core.language"
}