package net.unsweets.gamma.domain.model.io

import android.net.Uri

data class UpdateUserImageInputData(
    val uri: Uri?, // == null equals delete
    val type: Type
) {
    enum class Type { Avatar, Cover }
}
