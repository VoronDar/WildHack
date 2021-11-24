package com.astery.thisapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserToken(@Json(name = "access_token") var accessToken: String,
                     @Json(name = "refresh_token") var refreshToken: String,
                     @Json(name = "expires_in") var expiresIn: Int
)