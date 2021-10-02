package com.example.check_in_speaker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.check_in_speaker.databinding.ActivityMainBinding
import com.example.check_in_speaker.databinding.DialogSafeNumberBinding
import com.example.check_in_speaker.util.PreferencesUtil
import com.example.check_in_speaker.db.User
import com.example.check_in_speaker.viewmodel.MainViewModel
import com.example.check_in_speaker.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var audioManager: AudioManager
    private lateinit var dialog: Dialog
    private lateinit var prefs: PreferencesUtil
    private val mainViewModel: MainViewModel by viewModels {
        UserViewModelFactory((application as MainApplication).repository)
    }

    private var dialogBinding: DialogSafeNumberBinding? = null
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

        initAudioManager()

        mainViewModel.isClickCheckInButton.observe(this) {
            isClicked = it
        }

        mainViewModel.isValidSafeNumber.observe(this){
            if(it){
                prefs.setString("safeNumber", dialogBinding!!.dialogEtSafeNumber.text.toString())
                binding.tvMainSafeNumber.text = prefs.getString("safeNumber", "")
                dialog.dismiss()
            }else{
                Toast.makeText(this, "유효하지 않는 안심번호입니다. 다시 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
        
        /**
         * TODO() user 정보 가져오기.
         */
        mainViewModel.allUser.observe(this, { users ->
            users?.let {
//                Log.d("LOG", users[0].address + " " + users[0].date)
            }
        })

        binding.btnMainCheckin.setOnClickListener {
            clickCheckIn()
        }
        
        prefs = PreferencesUtil(applicationContext)

        if(prefs.getString("safeNumber","") == ""){
            initDialog()
            dialog.show()
        }else{
            binding.tvMainSafeNumber.text = prefs.getString("safeNumber", "")
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
            /**
             * TODO() user 정보 데이터베이스에 저장
             */
            val user = User(null, "seoul", "20211003")
            mainViewModel.insertUser(user)
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
        setVolumePauseAndDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        setVolumePauseAndDestroy()
    }

    private fun isVolumeSameInitVolume() : Boolean {
        val nowVolume = audioManager.getStreamVolume(
            AudioManager.STREAM_MUSIC
        )

        return initVolume == nowVolume
    }

    private fun setVolumePauseAndDestroy() {
        if(!isVolumeSameInitVolume()) {
            setInitVolume()
        } else {
            mainViewModel.focusStatusIsFailed()
        }
    }

    private fun initDialog(){
        dialogBinding = DialogSafeNumberBinding.inflate(LayoutInflater.from(baseContext))

        dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(dialogBinding!!.root)
        }

        dialogBinding!!.dialogTvOk.setOnClickListener {
            mainViewModel.isValidSafeNumber(dialogBinding!!.dialogEtSafeNumber.text.toString())
        }
    }
}