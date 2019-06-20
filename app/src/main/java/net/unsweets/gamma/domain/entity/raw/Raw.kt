package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable


abstract class Raw<T : Raw.RawValue> : Parcelable {
    abstract val type: String
    abstract val value: T

    interface RawValue
}
