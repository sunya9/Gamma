package net.unsweets.gamma.api.model

data class IDs(val ids: List<String>) {
    override fun toString(): String = ids.joinToString(",")
}
