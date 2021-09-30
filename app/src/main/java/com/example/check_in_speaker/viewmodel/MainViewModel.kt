package com.example.check_in_speaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import euphony.lib.transmitter.EuTxManager

class MainViewModel : ViewModel() {

    private var _isClickCheckInButton = MutableLiveData(true)
    val isClickCheckInButton : LiveData<Boolean>
        get() = _isClickCheckInButton
    private val mTxManager: EuTxManager by lazy {
        EuTxManager()
    }

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


}