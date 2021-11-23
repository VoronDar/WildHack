package com.astery.thisapp.remoteStorage

import com.astery.thisapp.model.UserAccess
import com.astery.thisapp.model.UserToken
import timber.log.Timber
import javax.inject.Inject


class RemoteDataStorage @Inject constructor(
    private val retrofit: RetrofitInstance
) {

    @Throws(RemoteWrongArgumentException::class, RemoteUnexpectedException::class)
    suspend fun auth(user: UserAccess): UserToken {
        val token: UserToken
        try {
            token = retrofit.api.login(user)
        } catch (e: retrofit2.HttpException) {
            Timber.d("login: failure response code " + e.code().toString() + " with input $user")
            if (e.code() == 401 || e.code() == 422)
                throw RemoteWrongArgumentException()
            throw RemoteUnexpectedException()
        }
        return token
    }
}

class RemoteUnexpectedException : Exception()
class RemoteWrongArgumentException : Exception()
class RemoteAccessDeniedException : Exception()