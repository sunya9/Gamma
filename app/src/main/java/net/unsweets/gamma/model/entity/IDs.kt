package net.unsweets.gamma.model.entity

data class IDs(val ids: List<String>) {
    override fun toString(): String = ids.joinToString(",")
}
