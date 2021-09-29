package com.example.check_in_speaker

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.check_in_speaker.databinding.ActivityMainBinding
import com.example.check_in_speaker.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var audioManager: AudioManager

    private var isClicked : Boolean = false
    private var initVolume = 0
    private var safeNumber = "hello"
    private var audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if(focusChange == AudioManager.AUDIOFOCUS_GAIN || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if(!isClicked) {
                buttonVisibility(isClicked)
                mainViewModel.onClickCheckInButton()
                setInitVolume()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initAudioManager()

        mainViewModel.isClickCheckInButton.observe(this) {
            isClicked = it
        }

        binding.btnMainCheckin.setOnClickListener {
            clickCheckIn()
        }
    }

    private fun initAudioManager() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        initVolume = audioManager.getStreamVolume(
            AudioManager.STREAM_MUSIC
        )

        initAudioFocusRequest()
    }

    private fun initAudioFocusRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setWillPauseWhenDucked(true)
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener)
                build()
            }
        }
    }

    private fun clickCheckIn() {
        if (isClicked) {
            setAudioFocusRequest()
        } else {
            setInitVolume()
        }

        buttonVisibility(isClicked)
        mainViewModel.onClickCheckInButton()
    }

    /**
     * TODO Modify 'safeNumber' (change SharedPreference value)
     */
    @SuppressLint("NewApi")
    private fun setAudioFocusRequest() {
        when(audioManager.requestAudioFocus(audioFocusRequest)) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                setCheckInVolume()
                mainViewModel.focusStatusIsGranted(safeNumber)
            }
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                return
            }
        }
    }

    private fun setCheckInVolume() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(((AudioManager.STREAM_MUSIC * 0.90).toInt())),
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    private fun setInitVolume() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            initVolume,
            AudioManager.FLAG_PLAY_SOUND
        )

        mainViewModel.focusStatusIsFailed()
    }

    private fun buttonVisibility(isClick : Boolean) {
        if(isClick) {
            binding.progressBarMainCheckin.visibility = View.VISIBLE
            binding.btnMainCheckin.text = "체크인 종료"
        } else {
            binding.progressBarMainCheckin.visibility = View.INVISIBLE
            binding.btnMainCheckin.text = "체크인 시작"
        }
    }

    override fun onPause() {
        super.onPause()
        if(!isClicked) {
            buttonVisibility(isClicked)
            mainViewModel.onClickCheckInButton()
        }
        setInitVolume()
    }

    override fun onDestroy() {
        super.onDestroy()
        setInitVolume()
    }
}