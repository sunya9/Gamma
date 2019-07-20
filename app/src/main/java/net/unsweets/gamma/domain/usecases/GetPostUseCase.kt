package net.unsweets.gamma.domain.usecases

import android.util.Log
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.GetPostInputData
import net.unsweets.gamma.domain.model.io.GetPostOutputData
import net.unsweets.gamma.domain.model.params.single.SearchPostParam
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetPostUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<GetPostOutputData, GetPostInputData>() {
    override suspend fun run(params: GetPostInputData): GetPostOutputData {
        val streamType = params.streamType
        val param = params.params
        val res = when (streamType) {
            is StreamType.Home -> pnutRepository.getHomeStream(param)
            is StreamType.Mentions -> pnutRepository.getMentionStream(param)
            is StreamType.Stars -> pnutRepository.getStars(streamType.userId, param)
            is StreamType.Tag -> pnutRepository.getTagStream(streamType.tag, param)
            is StreamType.User -> pnutRepository.getUserPosts(streamType.userId, param)
            is StreamType.Thread -> pnutRepository.getThread(streamType.postId, param)
            is StreamType.Explore.Conversations -> pnutRepository.getConversations(param)
            is StreamType.Explore.MissedConversations -> pnutRepository.getMissedConversations(param)
            is StreamType.Explore.Newcomers -> pnutRepository.getNewcomers(param)
            is StreamType.Explore.Photos -> pnutRepository.getPhotos(param)
            is StreamType.Explore.Trending -> pnutRepository.getTrending(param)
            is StreamType.Explore.Global -> pnutRepository.getGlobal(param)
            is StreamType.Search -> pnutRepository.searchPosts(params.params.apply {
                add(SearchPostParam(streamType.keyword))
                Log.e("params", SearchPostParam(streamType.keyword).toString())
            })
        }
        return GetPostOutputData(res)
    }

}