package net.unsweets.gamma.domain.repository

import android.content.Context
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.entity.TokenJsonAdapter
import net.unsweets.gamma.util.MoshiSingleton
import java.io.File
import java.io.FileInputStream

class PnutCacheRepository(currentUserId: String?, context: Context) : IPnutCacheRepository {
    private val baseCacheDir =
        File(File(context.cacheDir, "userCache"), currentUserId.orEmpty()).apply {
            mkdirs()
        }

    private enum class CachePath {
        Token
    }

    override suspend fun getToken(): Token? {
        return try {
            val file = CachePath.Token.getFile()
            if (!file.exists()) return null
            val inputStream = FileInputStream(file)
            inputStream.reader().use {
                val json = it.readText()
                TokenJsonAdapter(MoshiSingleton.moshi).fromJson(json)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun CachePath.getFile(): File {
        return File(baseCacheDir, name)
    }

    override suspend fun storeToken(token: Token) {
        val file = CachePath.Token.getFile()
        val json = TokenJsonAdapter(MoshiSingleton.moshi).toJson(token)
        file.writer().use {
            it.write(json)
        }
    }
}