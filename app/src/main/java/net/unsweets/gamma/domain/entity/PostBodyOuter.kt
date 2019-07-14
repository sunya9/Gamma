package net.unsweets.gamma.domain.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostBodyOuter(
    val postBody: PostBody,
    val files: List<Uri> = emptyList()
) : Parcelable