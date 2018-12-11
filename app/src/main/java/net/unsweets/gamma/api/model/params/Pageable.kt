package net.unsweets.gamma.api.model.params

data class Pageable(
    override val minId: String? = null,
    override val maxId: String? = null,
    override val count: Int? = null
) : IPageable