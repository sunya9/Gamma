package net.unsweets.gamma.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Channel
import net.unsweets.gamma.domain.model.io.GetChannelInputData
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.usecases.GetChannelUseCase
import net.unsweets.gamma.presentation.adapter.channelmember.ChannelMember
import net.unsweets.gamma.presentation.util.ManipulateList
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.util.Resource
import net.unsweets.gamma.util.SingleLiveEvent

class ManageChannelMembersViewModel private constructor(
    private val channelId: String,
    pnutRepository: IPnutRepository
) : ViewModel() {
    private var channelMembersResourceInternal: Resource<List<ChannelMember>> =
        Resource.Loading()
    private val channel = MutableLiveData<Resource<Channel>>()
    val event = SingleLiveEvent<Event>()
    private val getChannelUseCase = GetChannelUseCase(pnutRepository) { output ->
        val channelResource = output.channel

        val channelMembers = channelResource.value?.let { channel ->
            mutableListOf<ChannelMember>().apply {
                channel.owner?.let {
                    add(ChannelMember.Header(R.string.owner))
                    add(
                        ChannelMember.Item(
                            Channel.LimitedUser(
                                it.username,
                                it.id,
                                it.name,
                                it.getAvatarUrl()
                            )
                        )
                    )
                }
                if (channel.acl.full.userIds.isNotEmpty()) {
                    add(ChannelMember.Header(R.string.full))
                    addAll(channel.acl.full.userIds.map { ChannelMember.Item(it) })
                }

                if (channel.acl.write.userIds.isNotEmpty()) {
                    add(ChannelMember.Header(R.string.write))
                    addAll(channel.acl.write.userIds.map { ChannelMember.Item(it) })
                }
                if (channel.acl.read.userIds.isNotEmpty()) {
                    add(ChannelMember.Header(R.string.read))
                    addAll(channel.acl.read.userIds.map { ChannelMember.Item(it) })
                }
            }
        } ?: emptyList()

        channelMembersResourceInternal = when (channelResource) {
            is Resource.Error -> Resource.Error(channelResource.throwable, channelMembers)
            is Resource.Loading -> Resource.Loading(channelMembers)
            is Resource.Success -> Resource.Success(channelMembers)
        }
        println("channelMembers $channelMembers")
        event.emit(Event.ManipulatedList(ManipulateList.Add(IntRange(0, channelMembers.size))))
        channel.postValue(channelResource)

    }
    val items
        get() = channelMembersResourceInternal

    fun title(context: Context): LiveData<String> = channel.map { it.value?.title(context) ?: "" }

    init {
        viewModelScope.launch {
            getChannelUseCase.run(GetChannelInputData(channelId))
        }
    }

    val fabVisibility = channel.switchMap {
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Util.getVisibility(it.value?.acl?.full?.you ?: false))
        }
    }

    sealed class Event {
        data class ManipulatedList(val manipulateList: ManipulateList) :
            Event()
    }

    class Factory(private val channelId: String, private val pnutRepository: IPnutRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManageChannelMembersViewModel(channelId, pnutRepository) as T
        }
    }
}