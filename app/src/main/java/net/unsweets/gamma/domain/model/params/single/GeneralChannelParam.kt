package net.unsweets.gamma.domain.model.params.single

import net.unsweets.gamma.util.toInt

data class GeneralChannelParam(
    val includeRead: Boolean = false,
    val channelTypes: String = "",
    val excludeChannelTypes: String = "",
    val includeMarker: Boolean = false,
    val includeInactive: Boolean = false,
    val includeRaw: Boolean = false,
    val includeChannelRaw: Boolean = false,
    val includeRecentMessage: Boolean = false,
    val includeLimitedUsers: Boolean = true
) : BaseParam {
    override fun toMap(): Map<String, String> = hashMapOf<String, String>().also { map ->
        map["include_read"] = includeRead.toInt().toString()
        map["channel_types"] = channelTypes
        map["exclude_channel_types"] = excludeChannelTypes
        map["include_marker"] = includeMarker.toInt().toString()
        map["include_inactive"] = includeInactive.toInt().toString()
        map["include_raw"] = includeRaw.toInt().toString()
        map["include_channel_raw"] = includeChannelRaw.toInt().toString()
        map["include_recent_message"] = includeRecentMessage.toInt().toString()
        map["include_raw"] = includeRaw.toInt().toString()
        map["include_limited_users"] = includeLimitedUsers.toInt().toString()
    }

}
