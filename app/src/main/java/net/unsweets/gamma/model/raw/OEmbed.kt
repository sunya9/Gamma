package net.unsweets.gamma.model.raw

import com.squareup.moshi.Json

data class OEmbed(override val value: OembedValue) : Raw.IRaw {
    override val type: String = "io.pnut.core.oembed"

    interface BaseOEmbed : Raw.RawValue {
        val type: String
        val version: String
        val width: Int
        val height: Int
    }

    interface OptionalField {
        @Json(name = "embeddable_url")
        val embeddableUrl: String?
        val title: String?
        @Json(name = "author_name")
        val authorName: String?
        @Json(name = "author_url")
        val authorUrl: String?
        @Json(name = "provider_name")
        val providerName: String?
        @Json(name = "provider_url")
        val providerUrl: String?
        @Json(name = "cache_age")
        val cacheAge: Int?
        @Json(name = "poster_url")
        val posterUrl: String?
        @Json(name = "thumbnail_url")
        val thumbnailUrl: String?
        @Json(name = "thumbnail_height")
        val thumbnailHeight: String?
        @Json(name = "thumbnail_width")
        val thumbnailWidth: String?
        val bitrate: Int?
        val release: Int?
        val license: String?
        val genre: String?
        @Json(name = "track_type")
        val trackType: TrackType?

        enum class TrackType(val type: String) {
            ORIGINAL("original"),
            REMIX("remix"),
            LIVE("live"),
            SPOKEN("spoken"),
            PODCAST("podcast"),
            DEMO("demo"),
            LOOP("loop"),
            SOUND_EFFECT("sound_effect"),
            SAMPLE("sample"),
            OTHER("other")
        }

    }

    sealed class OembedValue : BaseOEmbed {
        data class Photo(
            override val type: String,
            override val version: String,
            override val width: Int,
            override val height: Int,
            val url: String,
            @Json(name = "embeddable_url") override val embeddableUrl: String?,
            override val title: String?,
            @Json(name = "author_name") override val authorName: String?,
            @Json(name = "author_url") override val authorUrl: String?,
            @Json(name = "provider_name") override val providerName: String?,
            @Json(name = "provider_url") override val providerUrl: String?,
            @Json(name = "cache_age") override val cacheAge: Int?,
            @Json(name = "poster_url") override val posterUrl: String?,
            @Json(name = "thumbnail_url") override val thumbnailUrl: String?,
            @Json(name = "thumbnail_height") override val thumbnailHeight: String?,
            @Json(name = "thumbnail_width") override val thumbnailWidth: String?,
            override val bitrate: Int?,
            override val release: Int?,
            override val license: String?,
            override val genre: String?,
            @Json(name = "track_type") override val trackType: OptionalField.TrackType?
        ) : OembedValue(), OptionalField
        // TODO: video, html5video, audio, rich
    }
}