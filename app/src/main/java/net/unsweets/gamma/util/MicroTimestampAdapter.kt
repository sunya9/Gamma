package net.unsweets.gamma.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson


class MicroTimestampAdapter {
    @ToJson
    fun toJson(@MicroTimestamp long: Long): String {
        return long.toString()
    }

    @FromJson
    @MicroTimestamp
    fun fromJson(json: String): Long {
        return json.toFloat().toLong()
    }
}