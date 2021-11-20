package com.astery.thisapp.remoteStorage

import com.astery.thisapp.model.UserAccess
import com.astery.thisapp.model.UserToken
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
        @Headers("Content-Type: application/json", "Media-Type: application/json", "Accept: application/json")
        @POST("auth/login")
        suspend fun login(@Body user:UserAccess): UserToken

}