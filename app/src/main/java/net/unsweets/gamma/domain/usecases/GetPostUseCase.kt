package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetPostInputData
import net.unsweets.gamma.domain.model.io.GetPostOutputData
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetPostUseCase(private val pnutRepository: IPnutRepository): UseCase<GetPostOutputData, GetPostInputData>() {
    override suspend fun run(input: GetPostInputData): GetPostOutputData {
        val streamType = input.streamType
        val params = input.params
        val res = when(streamType) {
            is StreamType.Home -> pnutRepository.getHomeStream(params)
            is StreamType.Mentions -> pnutRepository.getMentionStream(params)
            is StreamType.Stars -> pnutRepository.getStars(streamType.userId, params)
            is StreamType.Tag -> pnutRepository.getTagStream(streamType.tag, params)
            is StreamType.User -> pnutRepository.getUserPosts(streamType.userId, params)
            is StreamType.Explore.Conversations -> pnutRepository.getConversations(params)
            is StreamType.Explore.MissedConversations -> pnutRepository.getMissedConversations(params)
            is StreamType.Explore.Newcomers -> pnutRepository.getNewcomers(params)
            is StreamType.Explore.Photos -> pnutRepository.getPhotos(params)
            is StreamType.Explore.Trending -> pnutRepository.getTrending(params)
            is StreamType.Explore.Global -> pnutRepository.getGlobal(params)
        }
        return GetPostOutputData(res)
    }

}