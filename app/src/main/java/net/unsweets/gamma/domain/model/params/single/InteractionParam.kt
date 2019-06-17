package net.unsweets.gamma.domain.model.params.single

data class InteractionParam(
    val filters: Set<InteractionFilter>? = null,
    val exclude: Set<InteractionFilter>? = setOf(InteractionFilter.Reply)
): BaseParam {
    override fun toMap(): Map<String, String> {
        return hashMapOf<String, String>().apply {
            if(filters != null) put("filters", filters.joinToString(",", transform = ::toParamValue))
            if(exclude != null) put("exclude", exclude.joinToString(",", transform = ::toParamValue))
        }
    }

    private fun toParamValue(interactionFilter: InteractionFilter) = interactionFilter.value

    enum class InteractionFilter(val value: String) {
        Bookmark("bookmark"),
        Repost("repost"),
        Reply("reply"),
        Follow("follow"),
        PollResponse("poll_response")
    }
}