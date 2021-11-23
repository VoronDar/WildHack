package com.astery.thisapp.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astery.thisapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    @set:Inject
    lateinit var repository: Repository

    private val _isEntered: MutableLiveData<Boolean> = MutableLiveData(null)
    val isEntered: LiveData<Boolean>
        get() = _isEntered

    fun checkEntered(){
        viewModelScope.launch {
            _isEntered.value = repository.isEntered()
        }
    }

    fun logOut(){
        viewModelScope.launch {
            repository.logOut()
        }
    }



}
