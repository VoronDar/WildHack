package com.astery.thisapp.remoteStorage

import com.astery.thisapp.model.*
import retrofit2.http.*

interface ApiService {
        @Headers("Content-Type: application/json", "accept: application/json")
        @POST("auth/login")
        suspend fun login(@Body user:UserAccess): UserToken

        @Headers("Content-Type: application/json", "accept: application/json")
        @POST("auth/refresh")
        suspend fun refreshToken(@Body refreshToken: RefreshToken): UserToken

        @Headers("Content-Type: application/json", "accept: application/json")
        @GET("cams/")
        suspend fun getCams(@Header("Authorization") token:String): List<Camera>

        @Headers("Content-Type: application/json", "accept: application/json")
        @POST("cams/{cam_id}/hls/start")
        suspend fun getStreamUrlForCamera(@Path("cam_id") cameraId:Int, @Header("Authorization") token:String): CameraHsl
}