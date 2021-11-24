package com.astery.thisapp.remoteStorage

import com.astery.thisapp.model.*
import timber.log.Timber
import javax.inject.Inject


class RemoteDataStorage @Inject constructor(
    private val retrofit: RetrofitInstance
) {

    @kotlin.jvm.Throws(RemoteWrongArgumentException::class, RemoteUnexpectedException::class)
    suspend fun auth(user: UserAccess): UserToken {
        try {
            return  retrofit.api.login(user)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 422 || e.code() == 401){
                throw RemoteWrongArgumentException()
            }
            throw catchException(e)
        }
    }

    @kotlin.jvm.Throws(RemoteWrongArgumentException::class, RemoteUnexpectedException::class)
    suspend fun getCams(token:String): List<Camera> {
        try{
            //return retrofit.api.getCams(token)
            return retrofit.api.getCams(getBearerToken(token))
        } catch(e:retrofit2.HttpException){
            Timber.w("get cams: failure response ${e.localizedMessage}")
            throw catchException(e)
        }
    }

    suspend fun getStreamUrlForCamera(cameraId: Int, token: String): CameraHsl {
        try{
            return retrofit.api.getStreamUrlForCamera(cameraId, getBearerToken(token))
        } catch(e:retrofit2.HttpException){
            Timber.w("get stream url: failure response ${e.localizedMessage}")
            Timber.w("request url: ${e.response()?.raw()?.request?.url}")
            if (e.code() == 403) throw RemoteAccessDeniedException()
            throw catchException(e)
        }
    }

    suspend fun updateToken(refreshToken: String): UserToken {
        try {
            return  retrofit.api.refreshToken(RefreshToken(refreshToken))
        } catch (e: retrofit2.HttpException) {
            Timber.w("update token: failure response ${e.localizedMessage}")
            if (e.code() == 422 || e.code() == 401){
                throw RemoteWrongArgumentException()
            }
            throw catchException(e)
        }
    }


    private fun catchException(e:retrofit2.HttpException):java.lang.Exception{
        if (e.code() == 401) return RemoteAccessDeniedException()
        return RemoteUnexpectedException()
    }

    private fun getBearerToken(token:String):String{
        return "Bearer $token"
    }

}

class RemoteUnexpectedException : Exception()
class RemoteWrongArgumentException : Exception()
class RemoteAccessDeniedException : Exception()