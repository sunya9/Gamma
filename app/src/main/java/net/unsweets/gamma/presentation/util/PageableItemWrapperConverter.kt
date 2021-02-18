package net.unsweets.gamma.presentation.util

import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import net.unsweets.gamma.domain.entity.*
import net.unsweets.gamma.domain.model.CachedList
import net.unsweets.gamma.domain.model.PageableItemWrapper

sealed class PageableItemWrapperConverter {
    enum class Type { Item, Pager }

    abstract fun toCachedList(): CachedList<out UniquePageable>

    @JsonClass(generateAdapter = true)
    data class CachedPostList(
        val posts: List<StorablePost>
    ) : PageableItemWrapperConverter() {
        override fun toCachedList(): CachedList<Post> {
            val res = posts.map {
                when (it) {
                    is StorablePost.Item -> it.pageableItemWrapper
                    is StorablePost.Pager -> it.pager
                }
            }
            return CachedList(res)
        }

        companion object {
            fun createFromCachedList(postCachedList: List<PageableItemWrapper<Post>>): CachedPostList {
                val list = postCachedList.map {
                    when (it) {
                        is PageableItemWrapper.Pager<Post> -> StorablePost.Pager(it)
                        is PageableItemWrapper.Item<Post> -> StorablePost.Item(it)
                    }
                }
                return CachedPostList(list)
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class CachedUserList(
        val users: List<StorableUser>
    ) : PageableItemWrapperConverter() {
        override fun toCachedList(): CachedList<User> {
            val res = users.map {
                when (it) {
                    is StorableUser.Item -> it.pageableItemWrapper
                    is StorableUser.Pager -> it.pager
                }
            }
            return CachedList(res)
        }

        companion object {
            fun createFromCachedList(postCachedList: List<PageableItemWrapper<User>>): CachedUserList {
                val list = postCachedList.map {
                    when (it) {
                        is PageableItemWrapper.Pager<User> -> StorableUser.Pager(it)
                        is PageableItemWrapper.Item<User> -> StorableUser.Item(it)
                    }
                }
                return CachedUserList(list)
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class CachedInteractionList(
        val interactions: List<StorableInteraction>
    ) : PageableItemWrapperConverter() {
        override fun toCachedList(): CachedList<Interaction> {
            val res = interactions.map {
                when (it) {
                    is StorableInteraction.Item -> it.pageableItemWrapper
                    is StorableInteraction.Pager -> it.pager
                }
            }
            return CachedList(res)
        }

        companion object {
            fun createFromCachedList(postCachedList: List<PageableItemWrapper<Interaction>>): CachedInteractionList {
                val list = postCachedList.map {
                    when (it) {
                        is PageableItemWrapper.Pager<Interaction> -> StorableInteraction.Pager(it)
                        is PageableItemWrapper.Item<Interaction> -> StorableInteraction.Item(it)
                    }
                }
                return CachedInteractionList(list)
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class CachedMessageList(
        val messages: List<StorableMessage>
    ) : PageableItemWrapperConverter() {
        override fun toCachedList(): CachedList<Message> {
            val res = messages.map {
                when (it) {
                    is StorableMessage.Item -> it.pageableItemWrapper
                    is StorableMessage.Pager -> it.pager
                }
            }
            return CachedList(res)
        }

        companion object {
            fun createFromCachedList(postCachedList: List<PageableItemWrapper<Message>>): CachedMessageList {
                val list = postCachedList.map {
                    when (it) {
                        is PageableItemWrapper.Pager<Message> -> StorableMessage.Pager(it)
                        is PageableItemWrapper.Item<Message> -> StorableMessage.Item(it)
                    }
                }
                return CachedMessageList(list)
            }
        }
    }

    sealed class StorablePost {
        abstract val type: Type

        data class Item(val pageableItemWrapper: PageableItemWrapper.Item<Post>) :

            StorablePost() {
            override val type = Type.Item
        }

        data class Pager(val pager: PageableItemWrapper.Pager<Post>) : StorablePost() {
            override val type = Type.Pager
        }
    }

    sealed class StorableInteraction {
        abstract val type: Type

        data class Item(val pageableItemWrapper: PageableItemWrapper.Item<Interaction>) :
            StorableInteraction() {
            override val type = Type.Item
        }

        data class Pager(val pager: PageableItemWrapper.Pager<Interaction>) :
            StorableInteraction() {
            override val type = Type.Pager
        }
    }


    sealed class StorableUser {
        abstract val type: Type

        data class Item(val pageableItemWrapper: PageableItemWrapper.Item<User>) :
            StorableUser() {
            override val type = Type.Item
        }

        data class Pager(val pager: PageableItemWrapper.Pager<User>) :
            StorableUser() {
            override val type = Type.Pager
        }
    }

    sealed class StorableMessage(val type: Type) {
        data class Item(val pageableItemWrapper: PageableItemWrapper.Item<Message>) :
            StorableMessage(Type.Item)

        data class Pager(val pager: PageableItemWrapper.Pager<Message>) :
            StorableMessage(Type.Pager)
    }


    companion object {
        val storablePostAdapterFactory: PolymorphicJsonAdapterFactory<StorablePost> =
            PolymorphicJsonAdapterFactory.of(StorablePost::class.java, "type")
                .withSubtype(StorablePost.Item::class.java, Type.Item.name)
                .withSubtype(StorablePost.Pager::class.java, Type.Pager.name)

        val storableUserAdapterFactory: PolymorphicJsonAdapterFactory<StorableUser> =
            PolymorphicJsonAdapterFactory.of(StorableUser::class.java, "type")
                .withSubtype(StorableUser.Item::class.java, Type.Item.name)
                .withSubtype(StorableUser.Pager::class.java, Type.Pager.name)

        val storableInteractionAdapterFactory: PolymorphicJsonAdapterFactory<StorableInteraction> =
            PolymorphicJsonAdapterFactory.of(StorableInteraction::class.java, "type")
                .withSubtype(StorableInteraction.Item::class.java, Type.Item.name)
                .withSubtype(StorableInteraction.Pager::class.java, Type.Pager.name)

        val storableMessageFactory: PolymorphicJsonAdapterFactory<StorableMessage> =
            PolymorphicJsonAdapterFactory.of(StorableMessage::class.java, "type")
                .withSubtype(StorableMessage.Item::class.java, Type.Item.name)
                .withSubtype(StorableMessage.Pager::class.java, Type.Pager.name)
    }
}