package net.unsweets.gamma.util

import net.unsweets.gamma.R

sealed class ErrorCollections(val displayErrorMessageRes: Int) : Exception() {
    object CannotLoadFile : ErrorCollections(R.string.cannot_load_file)
    object AccountNotFound : ErrorCollections(R.string.account_not_found)
    object CannotOpenUrl : ErrorCollections(R.string.cannot_open_url)
}