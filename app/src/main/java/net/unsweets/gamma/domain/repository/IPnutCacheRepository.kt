package net.unsweets.gamma.domain.repository

import net.unsweets.gamma.domain.entity.Token

interface IPnutCacheRepository {
    suspend fun getToken(): Token?
    suspend fun storeToken(token: Token): Unit
}