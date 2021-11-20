package com.astery.thisapp.repository

import com.astery.thisapp.model.UserAccess
import com.astery.thisapp.remoteStorage.RemoteDataStorage
import com.astery.thisapp.ui.fragments.login.JobState
import javax.inject.Inject

open class Repository @Inject constructor(private val remoteStorage:RemoteDataStorage) {
    suspend fun auth(login: String, password: String): JobState {
        remoteStorage.auth(UserAccess(login, password))
        return JobState.Failure
    }
}