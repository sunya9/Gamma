package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
object RawImpl : Raw<Raw.RawValue>, Parcelable {
    override val type: String = ""
    override val value: Raw.RawValue = object : Raw.RawValue {}
}