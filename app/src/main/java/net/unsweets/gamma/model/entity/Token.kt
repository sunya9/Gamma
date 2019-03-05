package net.unsweets.gamma.model.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Token (
    val app: Client,
    val scopes: List<Scope>,
    val user: User,
    val storage: Storage
) : Parcelable {
    enum class Scope {
        @Json(name = "basic") BASIC,
        @Json(name = "stream") STREAM,
        @Json(name = "write_post") WRITE_POST,
        @Json(name = "follow") FOLLOW,
        @Json(name = "update_profile") UPDATE_PROFILE,
        @Json(name = "presence") PRESENCE,
        @Json(name = "messages") MESSAGES,
        @Json(name = "public_messages") PUBLIC_MESSAGES,
        @Json(name = "files") FILES,
        @Json(name = "polls") POLLS,
        @Json(name = "email") EMAIL
    }

    @Parcelize
    data class Storage(val available: Long, val total: Long) : Parcelable
}


