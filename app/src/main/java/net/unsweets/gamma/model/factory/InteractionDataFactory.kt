package net.unsweets.gamma.model.factory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import net.unsweets.gamma.model.entity.Interaction
import net.unsweets.gamma.model.source.InteractionDataSource

class InteractionDataFactory(private val context: Context) : DataSource.Factory<String, Interaction>() {
    private val items = MutableLiveData<InteractionDataSource>()
    fun getItems(): LiveData<InteractionDataSource> = items

    override fun create(): DataSource<String, Interaction> {
        val source = InteractionDataSource(context)
        items.postValue(source)
        return source
    }

}