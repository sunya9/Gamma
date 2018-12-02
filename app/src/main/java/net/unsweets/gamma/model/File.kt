package net.unsweets.gamma.model

import com.squareup.moshi.Json
import java.util.*

class File(
    @Json(name = "audio_info") val audioInfo: AudioInfo?,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "file_token") val fileToken: String,
    @Json(name = "file_token_read") val fileTokenRead: String,
    val id: String,
    @Json(name =  "image_info") val imageInfo: ImageInfo?,
    @Json(name = "is_complete") val isComplete: Boolean,
    @Json(name = "is_public") val isPublic: Boolean,
    val kind: FileKind,
    val link: String,
    @Json(name = "link_expires_at") val linkExpiresAt: Date,
    @Json(name = "link_short") val linkShort: String,
    @Json(name = "mime_type") val mimeType: String,
    val name: String,
    val sha256: String,
    val size: Int,
    val source: Client,
    val type: String,
    @Json(name = "upload_parameters") val uploadParameters: UploadParameters,
//    TODO: implement derivativeFiles
//    val derivativeFiles: List<DerivativeFiles>,
    val user: User?
) {
    data class UploadParameters(val method: String, val url: String)

    enum class FileKind {
        AUDIO, IMAGE, OTHER
    }

    data class ImageInfo(val height: Int, val width: Int)

    data class AudioInfo(
        val duration: Int,
        val bitrate: Int
    )

}

