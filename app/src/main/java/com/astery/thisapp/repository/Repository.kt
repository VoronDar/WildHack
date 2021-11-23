package com.astery.thisapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.astery.thisapp.LocalStorage
import com.astery.thisapp.model.UserAccess
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