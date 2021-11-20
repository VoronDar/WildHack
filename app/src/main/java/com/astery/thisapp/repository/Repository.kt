package com.astery.thisapp.repository

import com.astery.thisapp.model.UserAccess
import com.astery.thisapp.remoteStorage.RemoteDataStorage
import com.astery.thisapp.states.JobState
import javax.inject.Inject

open class Repository @Inject constructor(private val remoteStorage:RemoteDataStorage) {
    suspend fun auth(login: String, password: String): JobState {
        return remoteStorage.auth(UserAccess(login, password))
    }
}