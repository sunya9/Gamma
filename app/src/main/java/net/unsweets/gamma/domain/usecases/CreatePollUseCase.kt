package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.CreatePollInputData
import net.unsweets.gamma.domain.model.io.CreatePollOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class CreatePollUseCase(private val pnutRepository: IPnutRepository) :
    UseCase<CreatePollOutputData, CreatePollInputData>() {
    override fun run(params: CreatePollInputData): CreatePollOutputData {
        val pollPostBody = params.pollPostBody
        val res = pnutRepository.createPoll(pollPostBody)
        return CreatePollOutputData(res.data)
    }
}