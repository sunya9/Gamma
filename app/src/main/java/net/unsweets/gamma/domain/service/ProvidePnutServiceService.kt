package net.unsweets.gamma.domain.service

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.data.PnutService
import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.entity.raw.*
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class ProvidePnutServiceService(
    private val context: Context,
    private val accountRepository: IAccountRepository,
    private val preferenceRepository: IPreferenceRepository
) : IProvidePnutServiceService {
    private val apiBaseUrl = "https://api.pnut.io/v0/"
    private val cacheSize: Long = 1024 * 1024 * 10


    // minimize instantiate because instantiate of retrofit is high cost
    private var defaultPnutService = run {
        val token = preferenceRepository.getDefaultAccountID()?.let { id ->
            accountRepository.getToken(id)
        }
        createPnutService(token)
    }

    // Call this function when change account
    override fun updateDefaultPnutService(accountId: String): Boolean {
        val token = accountRepository.getToken(accountId) ?: return false
        preferenceRepository.updateDefaultAccountID(accountId)
        defaultPnutService = createPnutService(token)
        return true
    }

    private fun createPnutService(token: String? = null): PnutService {
        val client = OkHttpClient.Builder()
        token?.let { client.addInterceptor((getAuthorizationHeaderInterceptor(it))) }

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            client.addInterceptor(logging)
        }
        val cache = Cache(context.cacheDir, cacheSize)
        client.cache(cache)
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(client.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PnutService::class.java)
    }

    private fun getAuthorizationHeaderInterceptor(token: String): Interceptor =
        Interceptor {
            val request = it.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            it.proceed(request)
        }

    private val moshi: Moshi
        get() = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(
                PolymorphicJsonAdapterFactory.of(Interaction::class.java, "action")
                    .withSubtype(Interaction.Repost::class.java, "repost")
                    .withSubtype(Interaction.PollResponse::class.java, "poll_response")
                    .withSubtype(Interaction.Reply::class.java, "reply")
                    .withSubtype(Interaction.Follow::class.java, "follow")
                    .withSubtype(Interaction.Bookmark::class.java, "bookmark")
            )
            .add(
                PolymorphicJsonAdapterFactory.of(Raw::class.java, "type")
                    .withSubtype(OEmbed::class.java, "io.pnut.core.oembed")
                    .withSubtype(Spoiler::class.java, "shawn.spoiler")
                    .withSubtype(LongPost::class.java, "nl.chimpnut.blog.post")
                    .withSubtype(PollNotice::class.java, "io.pnut.core.poll-notice")
                    .withDefaultValue(RawImpl())
            )
            .add(
                PolymorphicJsonAdapterFactory.of(OEmbed.BaseOEmbedRawValue::class.java, "type")
                    .withSubtype(OEmbed.Photo.PhotoValue::class.java, "photo")
                    .withSubtype(OEmbed.Video.VideoValue::class.java, "video")
            )
            .add(KotlinJsonAdapterFactory())
            .build()
}