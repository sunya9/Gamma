package net.unsweets.gamma.domain.entity

import java.io.Serializable

data class FileBody(
    val kind: Kind,
    val name: String,
    val isPublic: Boolean = true
) : Serializable {
    val type = "net.unsweets.gamma"

    enum class Kind {
        other,
        image,
        audio
    }
}