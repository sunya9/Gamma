package net.unsweets.gamma.util

// https://developer.android.com/jetpack/guide#addendum
sealed class Resource<T>(
    val value: T? = null,
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(val throwable: Throwable, data: T? = null) : Resource<T>(data)
}
