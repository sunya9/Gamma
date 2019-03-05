package net.unsweets.gamma.model.factory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import net.unsweets.gamma.model.entity.Post
import net.unsweets.gamma.model.net.PnutRepository
import net.unsweets.gamma.model.source.PostDataSource

class PostDataFactory(
    private val context: Context,
    private val streamType: PnutRepository.StreamType,
    val extra: String?
) : DataSource.Factory<String, Post>() {
    private val items = MutableLiveData<PostDataSource>()
    fun getItems(): LiveData<PostDataSource> = items

    override fun create(): DataSource<String, Post> {
        val source = PostDataSource(context, streamType, extra)
        items.postValue(source)
        return source
    }

}