package net.unsweets.gamma.domain.entity

data class IDs(val ids: List<String>) {
    override fun toString(): String = ids.joinToString(",")
}
