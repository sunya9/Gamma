package net.unsweets.gamma.util

import net.unsweets.gamma.BuildConfig

object Constants {
    const val Gamma = "Gamma"
    const val MaxPostTextLength = 256
    const val unknownError = "Unknown error"
    val unknownErrorException = Exception(unknownError)
    const val apiBaseUrl = "https://api.pnut.io/v0/"
    const val PlayStoreUrl: String = "market://details?id=${BuildConfig.APPLICATION_ID}"
}