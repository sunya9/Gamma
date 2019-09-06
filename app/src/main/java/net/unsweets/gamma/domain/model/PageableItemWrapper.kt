package net.unsweets.gamma.domain.model

import net.unsweets.gamma.domain.entity.Pageable
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Unique


sealed class PageableItemWrapper<D> where D : Pageable, D : Unique {
    val uniqueKey: String?
        get() = when (this) {
            is Item -> item.uniqueKey
            else -> null
        }


    data class Item<D>(val item: D) : PageableItemWrapper<D>() where D : Pageable, D : Unique
    data class Pager<D>(
        val maxId: String? = null,
        val minId: String? = null,
        val more: Boolean = true,
        val state: State = State.None
    ) : PageableItemWrapper<D>() where D : Pageable, D : Unique {
        sealed class State {
            object None : State()
            object Loading : State()
            object Error : State()
        }

        companion object {
            fun <D> createFromMeta(meta: PnutResponse.Meta) where D : Pageable, D : Unique =
                Pager<D>(
                    meta.max_id,
                    meta.min_id,
                    meta.more ?: true,
                    State.None
                )
        }
    }

    fun getSinceId(): String? {
        return when (this) {
            is Item -> item.paginationId
            is Pager -> maxId
        }
    }

    fun getBeforeId(): String? {
        return when (this) {
            is Item -> item.paginationId
            is Pager -> minId
        }
    }
}