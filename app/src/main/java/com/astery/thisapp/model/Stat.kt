package com.astery.thisapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stat(val masks:Int?)
