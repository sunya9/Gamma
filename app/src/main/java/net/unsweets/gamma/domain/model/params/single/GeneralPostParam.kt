package net.unsweets.gamma.domain.model.params.single

import net.unsweets.gamma.util.toInt

data class GeneralPostParam(
    val includeDeleted: Boolean = false,
    val includePostRaw: Boolean = true,
    val includeBookmarkedBy: Boolean = false,
    val includeRepostedBy: Boolean = false
): BaseParam {
    override fun toMap(): Map<String, String> = hashMapOf<String, String>().also { map ->
        map["include_deleted"] = includeDeleted.toInt().toString()
        map["include_post_raw"] = includePostRaw.toInt().toString()
        map["include_bookmarked_by"] = includeBookmarkedBy.toInt().toString()
        map["include_reposted_by"] = includeRepostedBy.toInt().toString()
    }

}
