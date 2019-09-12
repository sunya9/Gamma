package net.unsweets.gamma.domain.model

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.UniquePageable


sealed class PageableItemWrapper<DD : UniquePageable> {
    val uniqueKey: String?
        get() = when (this) {
            is Item -> item.uniqueKey
            else -> null
        }
    abstract val type: Type

    enum class Type { Item, Pager }

    data class Item<D : UniquePageable>(val item: D) : PageableItemWrapper<D>() {
        override val type = Type.Item
    }
//    data class Item<D : UniquePageable>(val item: UniquePageable): PageableItemWrapper<D>() {
//        override val type = Type.Item
//    }
//    abstract class Item<BB : UniquePageable>(open val item: BB) : PageableItemWrapper<BB>() {
////        override val type = Type.Item
////        abstract val itemType: ItemType
////        enum class ItemType { Post, Interaction, User }
////        data class PostItem(override val item: Post): Item<Post>(item) {
////            override val itemType = ItemType.Post
////        }
////
////        data class UserItem(override val item: User): Item<User>(item) {
////            override val itemType = ItemType.User
////        }
////        data class InteractionItem(override val item: Interaction): Item<Interaction>(item) {
////            override val itemType = ItemType.Interaction
////        }
//    }


    data class Pager<TT : UniquePageable>(
        val maxId: String? = null,
        val minId: String? = null,
        val more: Boolean = true,
        val state: State = State.None,
        val virtual: Boolean = false
    ) : PageableItemWrapper<TT>() {
        override val type = Type.Pager

        enum class State {
            None,
            Loading,
            Error
        }

        companion object {
            fun <CC : UniquePageable> createFromMeta(
                meta: PnutResponse.Meta,
                requestPager: PageableItemWrapper<CC>?
            ) =
                Pager<CC>(
                    meta.min_id,
                    requestPager?.getSinceId(),
                    meta.more ?: true,
                    State.None
                )
        }
    }

    fun getSinceId(): String? {
        return when (this) {
            is Item -> item.paginationId
            is Pager -> minId
        }
    }

    fun getBeforeId(): String? {
        return when (this) {
            is Item -> item.paginationId
            is Pager -> maxId
        }
    }
}