package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable

interface Raw<T : Raw.RawValue> : Parcelable {
    val type: String
    val value: T

    interface RawValue
}
