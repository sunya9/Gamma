package net.unsweets.gamma.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Token

object LoginUtil {
    private val scopes = arrayOf(
        Token.Scope.BASIC,
        Token.Scope.STREAM,
        Token.Scope.WRITE_POST,
        Token.Scope.FOLLOW,
        Token.Scope.UPDATE_PROFILE,
        Token.Scope.PRESENCE,
        Token.Scope.MESSAGES,
        Token.Scope.FILES,
        Token.Scope.POLLS
    )


    private fun createLoginURL(context: Context): String {
        val clientId = context.getString(R.string.client_id)
        val scopeStr = scopes.joinToString(",")
        return context.getString(R.string.authenticate_url, clientId, scopeStr)
    }

    fun getLoginIntent(context: Context): Intent {
        val url = createLoginURL(context)
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}