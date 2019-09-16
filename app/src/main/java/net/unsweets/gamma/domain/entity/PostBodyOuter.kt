package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.model.UriInfo

@Parcelize
data class PostBodyOuter(
    val accountId: String,
    val postBody: PostBody,
    val files: List<UriInfo> = emptyList(),
    val pollPostBody: PollPostBody? = null
) : Parcelable