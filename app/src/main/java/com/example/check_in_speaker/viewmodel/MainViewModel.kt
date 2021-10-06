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

    private val _isValidSafeNumber = MutableLiveData<Boolean>()
    val isValidSafeNumber: LiveData<Boolean>
      get() = _isValidSafeNumber

    private val _isClickHelpButton = MutableLiveData(false)
    val isClickHelpButton : LiveData<Boolean>
        get() = _isClickHelpButton

    private val mTxManager: EuTxManager by lazy {
        EuTxManager()
    }

    val allUser: LiveData<List<User>> = repository.allUsers.asLiveData()

    fun onClickCheckInButton() {
        _isClickCheckInButton.value = _isClickCheckInButton.value != true
    }

    fun onClickHelpButton() {
        _isClickHelpButton.value = _isClickHelpButton.value != true
    }

    private val safeNumberFormat = Regex("^[가-힣][0-9]{2}[가-힣][0-9]{2}\$")

    fun isValidSafeNumber(safeNumber: String){
        _isValidSafeNumber.value = safeNumber.matches(safeNumberFormat)
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