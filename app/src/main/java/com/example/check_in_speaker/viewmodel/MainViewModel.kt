package com.example.check_in_speaker.viewmodel

import androidx.lifecycle.*
import com.example.check_in_speaker.db.User
import com.example.check_in_speaker.repository.UserRepository
import euphony.lib.transmitter.EuTxManager
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private var _isClickCheckInButton = MutableLiveData(true)
    val isClickCheckInButton : LiveData<Boolean>
        get() = _isClickCheckInButton

    private val mTxManager: EuTxManager by lazy {
        EuTxManager()
    }

    val allUser: LiveData<List<User>> = repository.allUsers.asLiveData()

    fun onClickCheckInButton() {
        _isClickCheckInButton.value = _isClickCheckInButton.value != true
    }

    fun focusStatusIsFailed() {
        mTxManager.stop()
    }

    fun focusStatusIsGranted(safeNumber : String) {
        mTxManager.euInitTransmit(safeNumber)
        mTxManager.process(-1)
    }

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insert(user)
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}