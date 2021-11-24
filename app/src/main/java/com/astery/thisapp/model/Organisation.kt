package com.astery.thisapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Organisation(val id:Int, val name:String)