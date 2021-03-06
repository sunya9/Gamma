package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class File(
  @Json(name = "audio_info") val audioInfo: AudioInfo? = null,
  @Json(name = "created_at") val createdAt: Date,
  @Json(name = "file_token") val fileToken: String? = null,
  @Json(name = "file_token_read") val fileTokenRead: String? = null,
  val id: String,
  @Json(name = "image_info") val imageInfo: ImageInfo? = null,
  @Json(name = "is_complete") val isComplete: Boolean,
  @Json(name = "is_public") val isPublic: Boolean,
  val kind: FileKind,
  val link: String? = null,
  @Json(name = "link_expires_at") val linkExpiresAt: Date? = null,
  @Json(name = "link_short") val linkShort: String? = null,
  @Json(name = "mime_type") val mimeType: String? = null,
  val name: String,
  val sha256: String,
  val size: Int,
  val source: Client,
  val type: String,
  @Json(name = "upload_parameters") val uploadParameters: UploadParameters? = null,
//    TODO: implement derivativeFiles
//    val derivativeFiles: List<DerivativeFiles>,
  val user: User? = null,
  @Json(name = "pagination_id") override val paginationId: String? = null
) : UniquePageable, Parcelable {
    @IgnoredOnParcel
    override val uniqueKey: String by lazy { id }

    @Parcelize
    data class UploadParameters(val method: String, val url: String) : Parcelable

    enum class FileKind {
        @Json(name = "audio") AUDIO, @Json(name = "image") IMAGE, @Json(name = "other") OTHER
    }

    @Parcelize
    data class ImageInfo(val height: Int, val width: Int) : Parcelable

    @Parcelize
    data class AudioInfo(
        val duration: Int,
        val bitrate: Int
    ) : Parcelable

}

