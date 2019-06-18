package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetFilesInputData
import net.unsweets.gamma.domain.model.io.GetFilesOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetFilesUseCase(val pnutRepository: IPnutRepository) : AsyncUseCase<GetFilesOutputData, GetFilesInputData>() {
    override suspend fun run(params: GetFilesInputData): GetFilesOutputData {
        val filesRes = pnutRepository.getFiles(params.getFilesParam)
        return GetFilesOutputData(filesRes)
    }

}
