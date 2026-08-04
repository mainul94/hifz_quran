package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiService
import com.example.data.api.RecitationAnalysisResult
import com.example.data.db.AppDatabase
import com.example.data.db.MemorizationProgressEntity
import com.example.data.db.VoiceNoteEntity
import com.example.data.model.Ayah
import com.example.data.model.DEFAULT_RECITERS
import com.example.data.model.LeaderboardUser
import com.example.data.model.MemorizationStatus
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.repository.MemorizationRepository
import com.example.data.repository.QuranRepository
import com.example.service.AudioPlayerManager
import com.example.service.AudioRecorderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MuhaffidhMaskMode(val label: String) {
    SHOW_ALL("Show Text"),
    HIDE_ALL("Blind Mode"),
    HIDE_ALTERNATE("Hide Alternate"),
    FIRST_LETTER("First Letter Only")
}

enum class DisplayMode(val label: String, val description: String) {
    VERSE_BY_VERSE("Verse by Verse", "Card view per verse with actions"),
    READING_MODE("Reading Mode", "Continuous Mushaf page layout; tap verse for popover")
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val quranRepository = QuranRepository()
    private val database = AppDatabase.getDatabase(application)
    private val memorizationRepository = MemorizationRepository(database)
    val audioPlayerManager = AudioPlayerManager(application)
    val audioRecorderManager = AudioRecorderManager(application)

    val surahList: List<Surah> = quranRepository.getAllSurahs()

    private val _selectedSurah = MutableStateFlow(surahList.first())
    val selectedSurah: StateFlow<Surah> = _selectedSurah.asStateFlow()

    private val _ayahs = MutableStateFlow<List<Ayah>>(emptyList())
    val ayahs: StateFlow<List<Ayah>> = _ayahs.asStateFlow()

    private val _selectedReciter = MutableStateFlow<Reciter>(DEFAULT_RECITERS.first())
    val selectedReciter: StateFlow<Reciter> = _selectedReciter.asStateFlow()

    private val _showWordByWord = MutableStateFlow(false)
    val showWordByWord: StateFlow<Boolean> = _showWordByWord.asStateFlow()

    private val _maskMode = MutableStateFlow(MuhaffidhMaskMode.SHOW_ALL)
    val maskMode: StateFlow<MuhaffidhMaskMode> = _maskMode.asStateFlow()

    private val _peekedWordKeys = MutableStateFlow<Set<String>>(emptySet())
    val peekedWordKeys: StateFlow<Set<String>> = _peekedWordKeys.asStateFlow()

    // --- Settings Preferences ---
    private val _displayMode = MutableStateFlow(DisplayMode.VERSE_BY_VERSE)
    val displayMode: StateFlow<DisplayMode> = _displayMode.asStateFlow()

    private val _showTranslation = MutableStateFlow(true)
    val showTranslation: StateFlow<Boolean> = _showTranslation.asStateFlow()

    private val _showTransliteration = MutableStateFlow(true)
    val showTransliteration: StateFlow<Boolean> = _showTransliteration.asStateFlow()

    private val _translationLanguage = MutableStateFlow("English")
    val translationLanguage: StateFlow<String> = _translationLanguage.asStateFlow()

    private val _arabicFontSize = MutableStateFlow(24f)
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val surahProgressList: StateFlow<List<MemorizationProgressEntity>> =
        memorizationRepository.allProgress.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        loadSurah(surahList.first().id)
    }

    fun setMaskMode(mode: MuhaffidhMaskMode) {
        _maskMode.value = mode
        _peekedWordKeys.value = emptySet()
    }

    fun setDisplayMode(mode: DisplayMode) {
        _displayMode.value = mode
    }

    fun setShowTranslation(show: Boolean) {
        _showTranslation.value = show
    }

    fun setShowTransliteration(show: Boolean) {
        _showTransliteration.value = show
    }

    fun setTranslationLanguage(language: String) {
        _translationLanguage.value = language
    }

    fun setArabicFontSize(fontSize: Float) {
        _arabicFontSize.value = fontSize
    }

    fun togglePeekWord(surahId: Int, ayahNum: Int, position: Int) {
        val key = "${surahId}_${ayahNum}_$position"
        val current = _peekedWordKeys.value
        _peekedWordKeys.value = if (current.contains(key)) current - key else current + key
    }

    fun loadSurah(surahId: Int) {
        val surah = surahList.find { it.id == surahId } ?: surahList.first()
        _selectedSurah.value = surah
        _ayahs.value = quranRepository.getAyahsForSurah(surah.id)
    }

    fun selectReciter(reciter: Reciter) {
        _selectedReciter.value = reciter
    }

    fun toggleWordByWord() {
        _showWordByWord.value = !_showWordByWord.value
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playAyahAudio(ayah: Ayah) {
        val audioUrl = quranRepository.getAudioUrl(
            surahId = ayah.surahId,
            ayahNumber = ayah.ayahNumber,
            reciterId = _selectedReciter.value.id
        )
        audioPlayerManager.playAudio(
            urlOrPath = audioUrl,
            title = "${_selectedSurah.value.nameEnglish} Ayah ${ayah.ayahNumber}"
        )
    }

    fun updateAyahStatus(surahId: Int, ayahNumber: Int, status: MemorizationStatus) {
        viewModelScope.launch {
            memorizationRepository.updateAyahStatus(surahId, ayahNumber, status)
        }
    }

    fun recordVoiceNote(surahId: Int, ayahNumber: Int, title: String) {
        viewModelScope.launch {
            val path = audioRecorderManager.startRecording(surahId, ayahNumber)
            // Path returned if recording started
        }
    }

    fun stopAndSaveVoiceNote(surahId: Int, ayahNumber: Int, title: String) {
        viewModelScope.launch {
            val result = audioRecorderManager.stopRecording()
            if (result != null) {
                val (filePath, durationMs) = result
                memorizationRepository.saveVoiceNote(
                    surahId = surahId,
                    ayahNumber = ayahNumber,
                    title = title,
                    filePath = filePath,
                    durationMs = durationMs,
                    notes = "Recorded practice session"
                )
            }
        }
    }
}

class AiAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiService()

    private val _isAnalyzingSpeech = MutableStateFlow(false)
    val isAnalyzingSpeech: StateFlow<Boolean> = _isAnalyzingSpeech.asStateFlow()

    private val _speechAnalysisResult = MutableStateFlow<RecitationAnalysisResult?>(null)
    val speechAnalysisResult: StateFlow<RecitationAnalysisResult?> = _speechAnalysisResult.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "Hafiz Guide" to "Assalamu Alaikum! I am your AI Tajweed & Memorization Assistant. Ask me any question regarding Tajweed rules (Ghunnah, Qalqalah, Ikhfa, Mad) or recitation tips!"
        )
    )
    val chatMessages: StateFlow<List<Pair<String, String>>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun evaluateRecitation(expectedAyahText: String, recitedText: String) {
        viewModelScope.launch {
            _isAnalyzingSpeech.value = true
            val result = geminiService.evaluateRecitation(expectedAyahText, recitedText)
            _speechAnalysisResult.value = result
            _isAnalyzingSpeech.value = false
        }
    }

    fun sendTajweedQuestion(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            val history = _chatMessages.value.filter { it.first != "Hafiz Guide" }
                .map { "User" to it.second }
            _chatMessages.value = _chatMessages.value + ("You" to question)
            _isChatLoading.value = true

            val response = geminiService.askTajweedChatbot(question, history)
            _chatMessages.value = _chatMessages.value + ("Hafiz Guide" to response)
            _isChatLoading.value = false
        }
    }
}

class VoiceNotesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = MemorizationRepository(database)
    val audioPlayerManager = AudioPlayerManager(application)

    val voiceNotes: StateFlow<List<VoiceNoteEntity>> = repository.allVoiceNotes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun deleteVoiceNote(id: Long) {
        viewModelScope.launch {
            repository.deleteVoiceNote(id)
        }
    }

    fun playVoiceNote(note: VoiceNoteEntity) {
        audioPlayerManager.playAudio(
            urlOrPath = note.filePath,
            title = note.title
        )
    }
}

class AnalyticsLeaderboardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = MemorizationRepository(database)

    val userProfile = repository.userProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val allProgress = repository.allProgress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val communityLeaderboard: List<LeaderboardUser> = repository.getCommunityLeaderboard()
}
