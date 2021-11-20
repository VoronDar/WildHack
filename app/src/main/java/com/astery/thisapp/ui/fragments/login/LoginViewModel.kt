package com.astery.thisapp.ui.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astery.thisapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val minLoginLength = 3
    private val minPasswordLength = 3

    @set:Inject
    lateinit var repository: Repository

    val login: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    private val _loginState: MutableLiveData<JobState> = MutableLiveData(JobState.Idle)
    /** the state of login progress */
    val loginState: LiveData<JobState>
        get() = _loginState

    
    /**
     * check the correctness of input and call repository.auth(login, password)
     *
     * make authState = Running (block every action)
     * if the input is incorrect make authState = Mistake
     * if there is an error response from repository (like no internet) make authState = Error
     * if there is no such a user make authState = Failure
     * if there is a success response from repository - authState = success
     * */
    fun auth() {
        viewModelScope.launch {
            _loginState.value = JobState.Running
            if (isInputEmpty()){
                _loginState.value = JobState.Mistake
                cancel()
            }
            _loginState.value = repository.auth(login.value!!, password.value!!)
        }
    }

    private fun isInputEmpty(): Boolean {
        if (login.value?.length!! < minLoginLength) return true
        if (password.value?.length!! < minPasswordLength) return true
        // maybe other logic there
        return false
    }
}

enum class JobState {
    /** didn't started */
    Idle,

    /** working */
    Running,
    Error,

    /** rejected got from repository */
    Failure,

    /** any simple mistake that can be handle right now */
    Mistake,
    Success
}
