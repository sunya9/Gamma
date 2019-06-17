package net.unsweets.gamma.domain.model.params.single

import net.unsweets.gamma.util.toInt

data class GeneralUserParam(
    val includeHtml: Boolean? = null,
    val includeUserHtml: Boolean? = null,
    val includeCounts: Boolean? = null,
    val includeUser: Boolean? = null,
    val includePresence: Boolean? = null,
    val includeRaw: Boolean? = null,
    val includeUserRaw: Boolean? = null
): BaseParam{
    override fun toMap(): Map<String, String> {
        return hashMapOf<String, String>().apply {
            if(includeHtml != null) put("include_html", includeHtml.toInt().toString())
            if(includeUserHtml != null) put("include_user_html", includeUserHtml.toInt().toString())
            if(includeCounts != null) put("include_counts", includeCounts.toInt().toString())
            if(includeUser != null) put("include_user", includeUser.toInt().toString())
            if(includePresence != null) put("include_presence", includePresence.toInt().toString())
            if(includeRaw != null) put("include_raw", includeRaw.toInt().toString())
            if(includeUserRaw != null) put("include_user_raw", includeUserRaw.toInt().toString())
        }
    }
}