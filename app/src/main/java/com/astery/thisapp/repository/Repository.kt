package com.astery.thisapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.astery.thisapp.LocalStorage
import com.astery.thisapp.ValueNotFoundException
import com.astery.thisapp.model.Camera
import com.astery.thisapp.model.CameraHsl
import com.astery.thisapp.model.UserAccess
import com.astery.thisapp.remoteStorage.RemoteAccessDeniedException
import com.astery.thisapp.remoteStorage.RemoteDataStorage
import com.astery.thisapp.remoteStorage.RemoteUnexpectedException
import com.astery.thisapp.remoteStorage.RemoteWrongArgumentException
import com.astery.thisapp.states.JError
import com.astery.thisapp.states.JFailure
import com.astery.thisapp.states.JSuccess
import com.astery.thisapp.states.JobState
import com.astery.thisapp.states.coreTypes.InternetConnectionError
import com.astery.thisapp.states.coreTypes.SomethingWentWrongError
import com.astery.thisapp.ui.fragments.login.LoginViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject


open class Repository @Inject constructor(
    private val remoteStorage: RemoteDataStorage,
    private val localStorage: LocalStorage,
    @ApplicationContext var context: Context
) {


    fun isEntered():Boolean{
        return localStorage.isEntered()
    }

    fun logOut(){
        localStorage.setEntered(false)
    }

    suspend fun login(login: String, password: String): JobState {
        if (!isOnline())
            return JError(InternetConnectionError())
        return try {
            val token = remoteStorage.auth(UserAccess(login, password))
            Timber.d("got token $token")

            localStorage.setToken(token.accessToken, Date().time + token.expiresIn)
            localStorage.setRefreshToken(token.refreshToken)
            localStorage.setEntered(true)
            JSuccess()
        } catch (e: RemoteWrongArgumentException) {
            JFailure(LoginViewModel.InvalidLoginInput.Both)
        } catch (e: RemoteUnexpectedException) {
            JError(SomethingWentWrongError())
        }
    }

    suspend fun getCams():List<Camera>{
        return try {
            remoteStorage.getCams(getToken())
        } catch (e:RemoteAccessDeniedException){
            getUpdatedToken()
            getCams()
        } catch (e:RemoteUnexpectedException){
            return listOf()
        }
    }

    suspend fun getStreamUrlForCamera(cameraId:Int):String{
        return try{
            remoteStorage.getStreamUrlForCamera(cameraId, getToken()).stream
        } catch (e:RemoteAccessDeniedException){
            getUpdatedToken()
            getStreamUrlForCamera(cameraId)
        }catch (e:RemoteUnexpectedException){
            TODO("find a way to exit")
        }
    }


    private suspend fun getToken():String{
        return try{
            localStorage.getToken(Date())
        } catch (e:ValueNotFoundException){
            Timber.d("there is no token in cache")
            getUpdatedToken()
        }
    }

    private suspend fun getUpdatedToken():String{
            // there might be an exception
            Timber.d("refresh token ${localStorage.getRefreshToken()}")
            val userToken = remoteStorage.updateToken(localStorage.getRefreshToken())
            localStorage.setRefreshToken(userToken.refreshToken)
            localStorage.setToken(userToken.accessToken, userToken.expiresIn + Date().time)
            return userToken.accessToken
    }







    private fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }

        }
        return false
    }
}