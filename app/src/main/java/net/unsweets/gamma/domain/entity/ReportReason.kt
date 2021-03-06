package net.unsweets.gamma.domain.entity

import com.squareup.moshi.Json

enum class ReportReason {
    @Json(name = "soliciting")
    Soliciting,
    @Json(name = "account_type")
    AccountType,
    @Json(name = "nsfw")
    Nsfw,
    @Json(name = "user_abuse")
    UserAbuse
}
