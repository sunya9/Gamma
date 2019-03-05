package net.unsweets.gamma.model.net

import net.unsweets.gamma.model.entity.params.Pageable

data class PnutServiceOption(val service: PnutService, val pageSize: Int) {
    val defaultPaging
        get() = Pageable(count = pageSize)
}