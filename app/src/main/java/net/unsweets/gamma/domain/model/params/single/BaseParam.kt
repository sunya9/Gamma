package net.unsweets.gamma.domain.model.params.single

interface BaseParam {
    fun toMap(): Map<String, String>
}