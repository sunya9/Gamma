package net.unsweets.gamma.domain.model

import androidx.annotation.StringRes
import net.unsweets.gamma.R

sealed class StreamType {
    object Home: StreamType()
    object Mentions: StreamType()
    data class Stars(val userId: String): StreamType()
    data class Tag(val tag: String): StreamType()
    data class User(val userId: String): StreamType()
    sealed class Explore(@StringRes val titleRes: Int): StreamType() {
        object Conversations : Explore(R.string.conversations)
        object MissedConversations : Explore(R.string.missed_conversations)
        object Newcomers : Explore(R.string.newcomers)
        object Photos : Explore(R.string.photos)
        object Trending : Explore(R.string.trending)
        object Global : Explore(R.string.global)
    }
}