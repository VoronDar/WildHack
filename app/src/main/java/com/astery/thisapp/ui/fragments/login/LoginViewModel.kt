package com.astery.thisapp.ui.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astery.thisapp.R
import com.astery.thisapp.repository.Repository
import com.astery.thisapp.states.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val minUsernameLenght = 3
    private val minPasswordLength = 3

    @set:Inject
    lateinit var repository: Repository

    val username: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    private val _loginState: MutableLiveData<JobState> = MutableLiveData(JIdle())
    /** the state of login progress */
    val loginState: LiveData<JobState>
        get() = _loginState

    
    /**
     * check the correctness of input and call repository.auth(login, password)
     *
     * make authState = Running (block every action)
     * if there is an error response from repository (like no internet) make authState = Error
     * if there is no such a user make authState = Failure
     * if there is a success response from repository - authState = success
     * */
    fun auth() {
        viewModelScope.launch {
            _loginState.value = JRunning()
            checkForMistakes()
            if (loginState.value is JRunning)
                _loginState.value = repository.auth(username.value!!, password.value!!)
        }
    }

    private fun isUsernameIncorrect():Boolean = (username.value?.length!! < minUsernameLenght)
    private fun isPasswordIncorrect():Boolean = (password.value?.length!! < minPasswordLength)

    private fun checkForMistakes(){
        if (isUsernameIncorrect() && isPasswordIncorrect()){
            _loginState.value = JFailure(InvalidLoginInput.Both)
        } else if (isUsernameIncorrect())
            _loginState.value = JFailure(InvalidLoginInput.Username)
        else if (isPasswordIncorrect()){
            _loginState.value = JFailure(InvalidLoginInput.Password)
        }
    }

    enum class InvalidLoginInput:JobFailureType{
        Username{
            override fun stringId(): Int  = R.string.login_failure_username
        },
        Password{
            override fun stringId(): Int = R.string.login_failure_password
        },
        Both{
            override fun stringId(): Int = R.string.login_failure_input
        }
    }

}
