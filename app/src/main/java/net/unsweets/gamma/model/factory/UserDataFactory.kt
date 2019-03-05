package net.unsweets.gamma.model.factory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.model.net.PnutRepository
import net.unsweets.gamma.model.source.UserDataSource

class UserDataFactory(
    private val context: Context,
    private val id: String,
    private val mode: PnutRepository.UserListMode
) : DataSource.Factory<String, User>() {
    private val items = MutableLiveData<UserDataSource>()
    fun getItems(): LiveData<UserDataSource> = items

    override fun create(): DataSource<String, User> {
        val source = UserDataSource(context, id, mode)
        items.postValue(source)
        return source
    }

}