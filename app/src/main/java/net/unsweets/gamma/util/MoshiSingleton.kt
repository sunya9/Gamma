package net.unsweets.gamma.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.entity.raw.*
import net.unsweets.gamma.domain.entity.raw.replacement.PostOEmbed
import net.unsweets.gamma.presentation.util.PageableItemWrapperConverter
import java.util.*

object MoshiSingleton {
    val moshi: Moshi = Moshi.Builder()
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
                .withSubtype(OEmbed::class.java, OEmbed.type)
                .withSubtype(Spoiler::class.java, Spoiler.type)
                .withSubtype(LongPost::class.java, LongPost.type)
                .withSubtype(PollNotice::class.java, PollNotice.type)
                .withSubtype(ChannelInvite::class.java, ChannelInvite.type)
                .withSubtype(RawImpl::class.java, RawImpl.type)
                .withDefaultValue(RawImpl())
        )
        .add(MicroTimestampAdapter())
        // for create post
        .add(
            PolymorphicJsonAdapterFactory.of(PostRaw::class.java, "type")
                .withSubtype(PostOEmbed::class.java, OEmbed.type)
                .withSubtype(Spoiler::class.java, Spoiler.type)
                .withSubtype(LongPost::class.java, LongPost.type)
                .withSubtype(ChannelInvite::class.java, ChannelInvite.type)
        )
        .add(
            PolymorphicJsonAdapterFactory.of(OEmbed.OEmbedRawValue::class.java, "type")
                .withSubtype(OEmbed.Photo.PhotoValue::class.java, OEmbed.Photo.PhotoValue.type)
                .withSubtype(OEmbed.Video.VideoValue::class.java, OEmbed.Video.VideoValue.type)
                .withSubtype(OEmbed.OEmbedRawValue::class.java, "")
                .withDefaultValue(OEmbed.OEmbedValueImpl)
        )
        .add(PageableItemWrapperConverter.storableUserAdapterFactory)
        .add(PageableItemWrapperConverter.storableInteractionAdapterFactory)
        .add(PageableItemWrapperConverter.storablePostAdapterFactory)
        .add(KotlinJsonAdapterFactory())
        .build()

}