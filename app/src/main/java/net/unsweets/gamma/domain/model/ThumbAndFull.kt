package net.unsweets.gamma.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThumbAndFull(val thumb: String, val full: String) : Parcelable
