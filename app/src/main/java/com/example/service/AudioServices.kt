package com.example.service

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackTitle = MutableStateFlow<String?>(null)
    val currentTrackTitle: StateFlow<String?> = _currentTrackTitle.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _repeatCountSetting = MutableStateFlow(1) // Repeat 1x, 3x, 5x, 10x
    val repeatCountSetting: StateFlow<Int> = _repeatCountSetting.asStateFlow()

    private var currentRepeatProgress = 0
    private var onTrackFinishedCallback: (() -> Unit)? = null

    fun playAudio(urlOrPath: String, title: String, onFinished: (() -> Unit)? = null) {
        stopAudio()
        onTrackFinishedCallback = onFinished
        currentRepeatProgress = 1
        _currentTrackTitle.value = title

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(urlOrPath)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    playbackParams = playbackParams.setSpeed(_playbackSpeed.value)
                }
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                }
                setOnCompletionListener {
                    if (currentRepeatProgress < _repeatCountSetting.value) {
                        currentRepeatProgress++
                        it.seekTo(0)
                        it.start()
                    } else {
                        _isPlaying.value = false
                        onTrackFinishedCallback?.invoke()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "MediaPlayer Error $what, $extra")
                    _isPlaying.value = false
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error playing audio: ${e.message}")
            _isPlaying.value = false
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resumeAudio() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        _isPlaying.value = false
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        mediaPlayer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && _isPlaying.value) {
                try {
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                } catch (e: Exception) {
                    Log.e("AudioPlayerManager", "Failed to set playback speed: ${e.message}")
                }
            }
        }
    }

    fun setRepeatCount(count: Int) {
        _repeatCountSetting.value = count
    }
}

class AudioRecorderManager(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    private var recordingStartTime: Long = 0L

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    fun startRecording(surahId: Int, ayahNumber: Int): String? {
        val voiceNotesDir = File(context.filesDir, "voice_notes").apply { if (!exists()) mkdirs() }
        val fileName = "vn_s${surahId}_a${ayahNumber}_${System.currentTimeMillis()}.m4a"
        val outputFile = File(voiceNotesDir, fileName)
        currentRecordingFile = outputFile

        return try {
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            recordingStartTime = System.currentTimeMillis()
            _isRecording.value = true
            outputFile.absolutePath
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Failed to start media recorder: ${e.message}")
            _isRecording.value = false
            null
        }
    }

    fun stopRecording(): Pair<String, Long>? {
        val recorder = mediaRecorder ?: return null
        val file = currentRecordingFile ?: return null

        val durationMs = System.currentTimeMillis() - recordingStartTime

        return try {
            recorder.stop()
            recorder.release()
            mediaRecorder = null
            _isRecording.value = false
            Pair(file.absolutePath, durationMs)
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error stopping recording: ${e.message}")
            recorder.release()
            mediaRecorder = null
            _isRecording.value = false
            null
        }
    }
}
