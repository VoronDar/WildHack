package com.astery.thisapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserToken(
    @field:Json(name = "access_token") var accessToken: String?,
    @field:Json(name = "refresh_token") var refreshToken: String?,
    @field:Json(name = "expires_in") var expiresIn: Int?
)