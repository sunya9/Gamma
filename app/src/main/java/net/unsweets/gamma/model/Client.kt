package net.unsweets.gamma.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(val name: String, val link: String, val id: String): Parcelable