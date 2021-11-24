package com.astery.thisapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CameraHsl(
    @Json(name = "source_id") val sourceId: String,
    val status: Boolean,
    val message: String?,
    val stream: String
)
