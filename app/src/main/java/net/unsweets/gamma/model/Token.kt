package net.unsweets.gamma.model

import com.squareup.moshi.Json

class Token (
    val app: Client,
    val scopes: List<Scope>,
    val user: User,
    val storage: Storage
) {
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

    data class Storage(val available: Long, val total: Long)
}


