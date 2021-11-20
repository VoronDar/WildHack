package com.astery.thisapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAccess(val username:String, val password:String)