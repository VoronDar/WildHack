package com.astery.thisapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Camera(val id:Int, val organisation: Organisation, val state:Boolean)

