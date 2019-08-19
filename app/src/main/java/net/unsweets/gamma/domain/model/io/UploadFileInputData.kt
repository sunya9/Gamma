package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.model.UriInfo
import java.io.InputStream

data class UploadFileInputData(
    val uriInfo: UriInfo,
    val inputStream: InputStream?
)
