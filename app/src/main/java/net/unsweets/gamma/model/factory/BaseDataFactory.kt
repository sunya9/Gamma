package net.unsweets.gamma.model.factory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

abstract class BaseDataFactory<T : DataSource<String, T>>(val context: Context) : DataSource.Factory<String, T>() {
    protected val items = MutableLiveData<T>()
    fun getItems(): LiveData<T> = items
}