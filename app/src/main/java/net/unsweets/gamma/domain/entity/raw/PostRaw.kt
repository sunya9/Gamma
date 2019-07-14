package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable

interface PostRaw<T : Raw.RawValue> : Parcelable {
    val type: String
    val value: T
}
