package net.unsweets.gamma.util

import net.unsweets.gamma.R
import java.io.IOException

sealed class ErrorCollections(val exception: Exception, val displayErrorMessageRes: Int) : Exception() {
    object CannotLoadFile : ErrorCollections(IOException(), R.string.cannot_load_file)
}