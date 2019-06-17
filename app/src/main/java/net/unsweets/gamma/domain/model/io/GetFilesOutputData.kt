package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.File
import net.unsweets.gamma.domain.entity.PnutResponse

data class GetFilesOutputData(
    val res: PnutResponse<List<File>>
)
