package com.example.check_in_speaker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.check_in_speaker.databinding.ActivityMainBinding
import com.example.check_in_speaker.databinding.DialogSafeNumberBinding
import com.example.check_in_speaker.db.User
import com.example.check_in_speaker.util.PreferencesUtil
import com.example.check_in_speaker.viewmodel.MainViewModel
import com.example.check_in_speaker.viewmodel.UserViewModelFactory
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

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
    private var safeNumber = ""
    private var currentPosition: LatLng? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
    private var audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if(focusChange == AudioManager.AUDIOFOCUS_GAIN || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if(!isClicked) {
                buttonVisibility(isClicked)
                mainViewModel.onClickCheckInButton()
                setInitVolume()
            }
        }
    }

    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val locationListener = LocationListener {
        it.let {
            currentPosition = LatLng(it.latitude, it.longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initLoadingDialog()
        initAudioManager()
        updateCurrentLocation()

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

        binding.btnMainCheckin.setOnClickListener {
            if(checkFindLocationPermission()){
                clickCheckIn()
            }else{
                requestGPSPermission()
            }
        }

        binding.tvMainVisitRecordHistory.setOnClickListener {
            startActivity(Intent(this, VisitRecordActivity::class.java))
        }

        prefs = PreferencesUtil(applicationContext)

        if(prefs.getString("safeNumber","") == ""){
            initDialog()
            dialog.show()
        }else{
            binding.tvMainSafeNumber.text = prefs.getString("safeNumber", "")
        }
    }

    private fun updateCurrentLocation(){
        if(checkFindLocationPermission()){
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                1f,
                locationListener
            )

            if(currentPosition == null){
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    4000,
                    1f,
                    locationListener
                )
            }
        }else{
            requestGPSPermission()
        }
    }

    private fun initLoadingDialog() {
        val fadingCircle: Sprite = FadingCircle()
        fadingCircle.color = resources.getColor(R.color.sky_blue, null)
        binding.progressBarMainCheckin.setIndeterminateDrawable(fadingCircle)
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
        val address = currentPosition?.let {
            getAddress(it)
        }

        if (isClicked) {
            if(address != null){
                val user = User(null, address, getCurrentDate())
                mainViewModel.insertUser(user)
                setAudioFocusRequest()
            }else{
                updateCurrentLocation()
                Toast.makeText(this, "현재 위치 불러오기에 실패했습니다. 잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            setInitVolume()
        }

        if(address != null){
            buttonVisibility(isClicked)
            mainViewModel.onClickCheckInButton()
        }
    }

    private fun getAddress(position: LatLng): String? {
        val geocoder = Geocoder(this, Locale.KOREA)
        return geocoder.getFromLocation(position.latitude, position.longitude, 1).first()
            .getAddressLine(0)
    }

    private fun getCurrentDate(): String{
        val now = System.currentTimeMillis()
        val date = Date(now)
        return dateFormat.format(date)
    }

    @SuppressLint("NewApi")
    private fun setAudioFocusRequest() {
        when(audioManager.requestAudioFocus(audioFocusRequest)) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                setCheckInVolume()
                safeNumber = prefs.getString("safeNumber", "")
                mainViewModel.focusStatusIsGranted(safeNumber)
            }
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                return
            }
        }
    }

    private fun checkFindLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestGPSPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
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

    companion object {
        const val PERMISSION_REQUEST_CODE = 2021
    }
}