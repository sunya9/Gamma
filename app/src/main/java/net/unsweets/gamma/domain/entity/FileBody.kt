package net.unsweets.gamma.domain.entity

import java.io.Serializable

data class FileBody(
  val kind: File.FileKind,
  val name: String,
  val isPublic: Boolean = true
) : Serializable {
    val type = "net.unsweets.gamma"
}