package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.MemorizationStatus
import com.example.ui.components.AudioPlayerBar
import com.example.ui.components.ReciterSelectorDialog
import com.example.ui.components.WordByWordDialog
import com.example.viewmodel.DisplayMode
import com.example.viewmodel.MuhaffidhMaskMode
import com.example.viewmodel.QuranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorizeScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSettings: (() -> Unit)? = null
) {
    val selectedSurah by viewModel.selectedSurah.collectAsState()
    val ayahs by viewModel.ayahs.collectAsState()
    val selectedReciter by viewModel.selectedReciter.collectAsState()
    val isPlaying by viewModel.audioPlayerManager.isPlaying.collectAsState()
    val currentTrackTitle by viewModel.audioPlayerManager.currentTrackTitle.collectAsState()
    val repeatCount by viewModel.audioPlayerManager.repeatCountSetting.collectAsState()
    val speed by viewModel.audioPlayerManager.playbackSpeed.collectAsState()
    val isRecording by viewModel.audioRecorderManager.isRecording.collectAsState()
    val surahProgressList by viewModel.surahProgressList.collectAsState()
    val maskMode by viewModel.maskMode.collectAsState()
    val peekedWordKeys by viewModel.peekedWordKeys.collectAsState()

    // Settings State
    val displayMode by viewModel.displayMode.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()
    val showTransliteration by viewModel.showTransliteration.collectAsState()
    val translationLanguage by viewModel.translationLanguage.collectAsState()
    val arabicFontSize by viewModel.arabicFontSize.collectAsState()

    var showReciterDialog by remember { mutableStateOf(false) }
    var selectedAyahForWordByWord by remember { mutableStateOf<Ayah?>(null) }
    var selectedAyahForPopover by remember { mutableStateOf<Ayah?>(null) }
    var surahDropdownExpanded by remember { mutableStateOf(false) }
    var recordingAyahKey by remember { mutableStateOf<String?>(null) }

    if (showReciterDialog) {
        ReciterSelectorDialog(
            selectedReciter = selectedReciter,
            onReciterSelected = {
                viewModel.selectReciter(it)
                showReciterDialog = false
            },
            onDismiss = { showReciterDialog = false }
        )
    }

    selectedAyahForWordByWord?.let { ayah ->
        WordByWordDialog(
            ayah = ayah,
            onDismiss = { selectedAyahForWordByWord = null }
        )
    }

    selectedAyahForPopover?.let { ayah ->
        val ayahKey = "${ayah.surahId}_${ayah.ayahNumber}"
        val progressEntity = surahProgressList.find { it.ayahKey == ayahKey }
        val currentStatus = progressEntity?.let {
            try {
                MemorizationStatus.valueOf(it.status)
            } catch (e: Exception) {
                MemorizationStatus.NEW
            }
        } ?: MemorizationStatus.NEW

        VerseOptionsPopoverBottomSheet(
            ayah = ayah,
            surahName = selectedSurah.nameEnglish,
            currentStatus = currentStatus,
            arabicFontSize = arabicFontSize,
            showTranslation = showTranslation,
            showTransliteration = showTransliteration,
            isRecordingThisAyah = isRecording && recordingAyahKey == ayahKey,
            onPlayAudio = { viewModel.playAyahAudio(ayah) },
            onWordByWord = {
                selectedAyahForWordByWord = ayah
                selectedAyahForPopover = null
            },
            onToggleRecording = {
                if (isRecording && recordingAyahKey == ayahKey) {
                    viewModel.stopAndSaveVoiceNote(
                        surahId = ayah.surahId,
                        ayahNumber = ayah.ayahNumber,
                        title = "Practice: ${selectedSurah.nameEnglish} v${ayah.ayahNumber}"
                    )
                    recordingAyahKey = null
                } else {
                    recordingAyahKey = ayahKey
                    viewModel.recordVoiceNote(
                        surahId = ayah.surahId,
                        ayahNumber = ayah.ayahNumber,
                        title = "Practice: ${selectedSurah.nameEnglish} v${ayah.ayahNumber}"
                    )
                }
            },
            onStatusChange = { newStatus ->
                viewModel.updateAyahStatus(ayah.surahId, ayah.ayahNumber, newStatus)
            },
            onDismiss = { selectedAyahForPopover = null }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentTrackTitle != null) {
                AudioPlayerBar(
                    trackTitle = currentTrackTitle,
                    isPlaying = isPlaying,
                    reciter = selectedReciter,
                    repeatCount = repeatCount,
                    playbackSpeed = speed,
                    onPlayPause = {
                        if (isPlaying) viewModel.audioPlayerManager.pauseAudio()
                        else viewModel.audioPlayerManager.resumeAudio()
                    },
                    onStop = { viewModel.audioPlayerManager.stopAudio() },
                    onReciterClick = { showReciterDialog = true },
                    onRepeatCountChange = { viewModel.audioPlayerManager.setRepeatCount(it) },
                    onSpeedChange = { viewModel.audioPlayerManager.setSpeed(it) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Header Row with Settings Quick Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "OF",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Assalamu Alaikum,",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Omar Farooq",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Streak Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "12 Days",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (onNavigateToSettings != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onNavigateToSettings,
                                modifier = Modifier.testTag("quick_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Current Surah Focus Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = if (displayMode == DisplayMode.READING_MODE) "MUSHAF READING MODE" else "REVISION DUE TODAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Box(modifier = Modifier.testTag("surah_selector_box")) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.clickable { surahDropdownExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Switch Surah",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = surahDropdownExpanded,
                                    onDismissRequest = { surahDropdownExpanded = false }
                                ) {
                                    viewModel.surahList.forEach { surah ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("${surah.id}. ${surah.nameEnglish}")
                                                    Text(surah.nameArabic, fontWeight = FontWeight.Bold)
                                                }
                                            },
                                            onClick = {
                                                viewModel.loadSurah(surah.id)
                                                surahDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Surah ${selectedSurah.id}. ${selectedSurah.nameEnglish}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${selectedSurah.translatedName} • ${selectedSurah.verseCount} Verses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Text(
                                text = selectedSurah.nameArabic,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { 0.65f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "65% Memorized",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Muhaffidh Masking Mode Bar
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Muhaffidh Masking Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (maskMode == MuhaffidhMaskMode.SHOW_ALL) "Normal Mode" else "Tap words to peek",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(MuhaffidhMaskMode.values()) { mode ->
                            val selected = mode == maskMode
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.setMaskMode(mode) },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (mode == MuhaffidhMaskMode.SHOW_ALL) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = mode.label, fontSize = 12.sp)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                ),
                                modifier = Modifier.testTag("mask_mode_chip_${mode.name}")
                            )
                        }
                    }
                }
            }

            // --- DISPLAY MODE SWITCHING ---
            if (displayMode == DisplayMode.READING_MODE) {
                // MUSHAF CONTINUOUS READING MODE
                item {
                    ReadingModeMushafView(
                        ayahs = ayahs,
                        arabicFontSize = arabicFontSize,
                        showTranslation = showTranslation,
                        maskMode = maskMode,
                        peekedWordKeys = peekedWordKeys,
                        onTogglePeek = { surahId, ayahNum, pos -> viewModel.togglePeekWord(surahId, ayahNum, pos) },
                        onVerseClick = { ayah -> selectedAyahForPopover = ayah }
                    )
                }
            } else {
                // VERSE BY VERSE CARDS
                items(ayahs) { ayah ->
                    val ayahKey = "${ayah.surahId}_${ayah.ayahNumber}"
                    val progressEntity = surahProgressList.find { it.ayahKey == ayahKey }
                    val currentStatus = progressEntity?.let {
                        try {
                            MemorizationStatus.valueOf(it.status)
                        } catch (e: Exception) {
                            MemorizationStatus.NEW
                        }
                    } ?: MemorizationStatus.NEW

                    AyahCardItem(
                        ayah = ayah,
                        maskMode = maskMode,
                        peekedWordKeys = peekedWordKeys,
                        arabicFontSize = arabicFontSize,
                        showTranslation = showTranslation,
                        showTransliteration = showTransliteration,
                        currentStatus = currentStatus,
                        isRecordingThisAyah = isRecording && recordingAyahKey == ayahKey,
                        onTogglePeek = { position -> viewModel.togglePeekWord(ayah.surahId, ayah.ayahNumber, position) },
                        onPlayAudio = { viewModel.playAyahAudio(ayah) },
                        onWordByWord = { selectedAyahForWordByWord = ayah },
                        onToggleRecording = {
                            if (isRecording && recordingAyahKey == ayahKey) {
                                viewModel.stopAndSaveVoiceNote(
                                    surahId = ayah.surahId,
                                    ayahNumber = ayah.ayahNumber,
                                    title = "Practice: ${selectedSurah.nameEnglish} v${ayah.ayahNumber}"
                                )
                                recordingAyahKey = null
                            } else {
                                recordingAyahKey = ayahKey
                                viewModel.recordVoiceNote(
                                    surahId = ayah.surahId,
                                    ayahNumber = ayah.ayahNumber,
                                    title = "Practice: ${selectedSurah.nameEnglish} v${ayah.ayahNumber}"
                                )
                            }
                        },
                        onStatusChange = { newStatus ->
                            viewModel.updateAyahStatus(ayah.surahId, ayah.ayahNumber, newStatus)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReadingModeMushafView(
    ayahs: List<Ayah>,
    arabicFontSize: Float,
    showTranslation: Boolean,
    maskMode: MuhaffidhMaskMode,
    peekedWordKeys: Set<String>,
    onTogglePeek: (Int, Int, Int) -> Unit,
    onVerseClick: (Ayah) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mushaf_reading_mode_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mushaf Reading Page",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tap verse for popover options",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continuous Flow Layout
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ayahs.forEach { ayah ->
                    val words = ayah.arabicText.split(" ")

                    // Render words
                    words.forEachIndexed { originalIndex, word ->
                        val wordKey = "${ayah.surahId}_${ayah.ayahNumber}_$originalIndex"
                        val isPeeked = peekedWordKeys.contains(wordKey)

                        val isMasked = when (maskMode) {
                            MuhaffidhMaskMode.SHOW_ALL -> false
                            MuhaffidhMaskMode.HIDE_ALL -> !isPeeked
                            MuhaffidhMaskMode.HIDE_ALTERNATE -> (originalIndex % 2 == 1) && !isPeeked
                            MuhaffidhMaskMode.FIRST_LETTER -> false
                        }

                        val displayText = when {
                            isPeeked || maskMode == MuhaffidhMaskMode.SHOW_ALL -> word
                            maskMode == MuhaffidhMaskMode.FIRST_LETTER -> {
                                if (word.isNotEmpty()) "${word.first()}..." else "..."
                            }
                            isMasked -> "🙈"
                            else -> word
                        }

                        Text(
                            text = displayText,
                            fontSize = arabicFontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMasked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .clickable { onTogglePeek(ayah.surahId, ayah.ayahNumber, originalIndex) }
                        )
                    }

                    // Verse End Ornament / Marker (Clickable to show popover for this verse)
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { onVerseClick(ayah) }
                            .testTag("verse_marker_${ayah.ayahNumber}")
                    ) {
                        Text(
                            text = " ۝ ${ayah.ayahNumber} ",
                            fontSize = (arabicFontSize * 0.65).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (showTranslation) {
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Translations Preview",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ayahs.forEach { ayah ->
                            Text(
                                text = "(${ayah.ayahNumber}) ${ayah.englishText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AyahCardItem(
    ayah: Ayah,
    maskMode: MuhaffidhMaskMode,
    peekedWordKeys: Set<String>,
    arabicFontSize: Float = 24f,
    showTranslation: Boolean = true,
    showTransliteration: Boolean = true,
    currentStatus: MemorizationStatus,
    isRecordingThisAyah: Boolean,
    onTogglePeek: (Int) -> Unit,
    onPlayAudio: () -> Unit,
    onWordByWord: () -> Unit,
    onToggleRecording: () -> Unit,
    onStatusChange: (MemorizationStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.ayahNumber}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Ayah Badge & Player Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Verse ${ayah.ayahNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Word-by-word button
                    IconButton(
                        onClick = onWordByWord,
                        modifier = Modifier.testTag("wbw_button_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Word-by-word",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Record practice button
                    IconButton(
                        onClick = onToggleRecording,
                        modifier = Modifier.testTag("record_button_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = if (isRecordingThisAyah) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Record Practice",
                            tint = if (isRecordingThisAyah) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Play Audio Button
                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("play_button_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Ayah Audio",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Muhaffidh Word-Masking Interactive Flow Layout (Right-to-Left)
            val words = ayah.arabicText.split(" ")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                words.reversed().forEachIndexed { indexFromEnd, word ->
                    val originalIndex = words.size - 1 - indexFromEnd
                    val wordKey = "${ayah.surahId}_${ayah.ayahNumber}_$originalIndex"
                    val isPeeked = peekedWordKeys.contains(wordKey)

                    val isMasked = when (maskMode) {
                        MuhaffidhMaskMode.SHOW_ALL -> false
                        MuhaffidhMaskMode.HIDE_ALL -> !isPeeked
                        MuhaffidhMaskMode.HIDE_ALTERNATE -> (originalIndex % 2 == 1) && !isPeeked
                        MuhaffidhMaskMode.FIRST_LETTER -> false
                    }

                    val displayText = when {
                        isPeeked || maskMode == MuhaffidhMaskMode.SHOW_ALL -> word
                        maskMode == MuhaffidhMaskMode.FIRST_LETTER -> {
                            if (word.isNotEmpty()) "${word.first()}..." else "..."
                        }
                        isMasked -> "🙈 [ ? ]"
                        else -> word
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMasked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clickable { onTogglePeek(originalIndex) }
                    ) {
                        Text(
                            text = displayText,
                            fontSize = arabicFontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMasked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (showTransliteration) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = ayah.transliteration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (showTranslation) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ayah.englishText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Memorization Status Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MemorizationStatus.values().forEach { status ->
                    val isSelected = currentStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusChange(status) },
                        label = {
                            Text(
                                text = status.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (status) {
                                MemorizationStatus.MASTERED -> MaterialTheme.colorScheme.primary
                                MemorizationStatus.MEMORIZED -> MaterialTheme.colorScheme.secondary
                                MemorizationStatus.REVIEWING -> Color(0xFFC28200)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Verse Options Popover for Reading Mode
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseOptionsPopoverBottomSheet(
    ayah: Ayah,
    surahName: String,
    currentStatus: MemorizationStatus,
    arabicFontSize: Float,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    isRecordingThisAyah: Boolean,
    onPlayAudio: () -> Unit,
    onWordByWord: () -> Unit,
    onToggleRecording: () -> Unit,
    onStatusChange: (MemorizationStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("verse_options_bottom_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Surah $surahName • Verse ${ayah.ayahNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Verse Options & Tools",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Arabic text preview
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = ayah.arabicText,
                        fontSize = arabicFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showTransliteration) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ayah.transliteration,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (showTranslation) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ayah.englishText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onPlayAudio()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("popover_play_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Recite", fontSize = 13.sp)
                }

                Button(
                    onClick = onWordByWord,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("popover_wbw_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Words", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onToggleRecording,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("popover_record_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (isRecordingThisAyah) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isRecordingThisAyah) Color.Red else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRecordingThisAyah) "Stop" else "Practice", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Memorization Status Update
            Text(
                text = "Memorization Progress",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MemorizationStatus.values().forEach { status ->
                    val isSelected = currentStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onStatusChange(status)
                            onDismiss()
                        },
                        label = { Text(status.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
