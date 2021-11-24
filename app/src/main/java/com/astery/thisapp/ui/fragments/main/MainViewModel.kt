package com.astery.thisapp.ui.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astery.thisapp.model.Camera
import com.astery.thisapp.model.Stat
import com.astery.thisapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    @set:Inject
    lateinit var repository: Repository

    private val _stat: MutableLiveData<Stat> = MutableLiveData(Stat(null))
    val stat: LiveData<Stat>
        get() = _stat

    private val _cameras: MutableLiveData<List<Camera>> = MutableLiveData()
    val cameras: LiveData<List<Camera>>
        get() = _cameras

    private val _streamUrl: MutableLiveData<String> = MutableLiveData()
    val streamUrl: LiveData<String>
        get() = _streamUrl

    /** load masks, helmets and ect statistic*/
    fun loadStatistic() {
        TODO("not yet implemented")
    }

    fun loadCams() {
        viewModelScope.launch {
            _cameras.value = repository.getCams()
        }
    }

    fun getStreamUrl(cameraId: Int) {
        viewModelScope.launch {
            _streamUrl.value = repository.getStreamUrlForCamera(cameraId)
        }
    }


}
