package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MemorizationProgressEntity
import com.example.data.model.MemorizationStatus
import com.example.data.quran.QuranDataProvider
import com.example.viewmodel.QuranViewModel

@Composable
fun RepetitionScheduleScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.surahProgressList.collectAsState()
    val surahs = viewModel.surahList

    val dueToday = progressList.filter {
        it.status == MemorizationStatus.NEW.name || it.status == MemorizationStatus.REVIEWING.name
    }
    val reviewingSoon = progressList.filter { it.status == MemorizationStatus.MEMORIZED.name }
    val mastered = progressList.filter { it.status == MemorizationStatus.MASTERED.name }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("repetition_schedule_screen")
    ) {
        // Daily Revision Goal Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Spaced Repetition Schedule",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Personalized memory retention planner",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Revision Goal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${dueToday.size} Verses Due",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progress = if (progressList.isEmpty()) 0f else (mastered.size.toFloat() / progressList.size.toFloat())
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Schedule Sections List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: Due Today
            item {
                Text(
                    text = "Due for Revision Today (${dueToday.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (dueToday.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "MashaAllah! No verses due for review right now. All caught up!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(dueToday) { progressItem ->
                    val surah = surahs.find { it.id == progressItem.surahId } ?: surahs.first()
                    ScheduleAyahItemCard(
                        surahName = surah.nameEnglish,
                        ayahNumber = progressItem.ayahNumber,
                        statusText = "1-Day Review Cycle",
                        buttonText = "Mark Revised",
                        onAction = {
                            viewModel.updateAyahStatus(
                                progressItem.surahId,
                                progressItem.ayahNumber,
                                MemorizationStatus.MEMORIZED
                            )
                        }
                    )
                }
            }

            // Section 2: Reviewing Soon
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reviewing Soon (7-Day Cycle) (${reviewingSoon.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            items(reviewingSoon) { progressItem ->
                val surah = surahs.find { it.id == progressItem.surahId } ?: surahs.first()
                ScheduleAyahItemCard(
                    surahName = surah.nameEnglish,
                    ayahNumber = progressItem.ayahNumber,
                    statusText = "Scheduled in 3 days",
                    buttonText = "Promote to Mastered",
                    onAction = {
                        viewModel.updateAyahStatus(
                            progressItem.surahId,
                            progressItem.ayahNumber,
                            MemorizationStatus.MASTERED
                        )
                    }
                )
            }

            // Section 3: Mastered
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mastered Verses (30-Day Cycle) (${mastered.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            items(mastered) { progressItem ->
                val surah = surahs.find { it.id == progressItem.surahId } ?: surahs.first()
                ScheduleAyahItemCard(
                    surahName = surah.nameEnglish,
                    ayahNumber = progressItem.ayahNumber,
                    statusText = "Strong Retention",
                    buttonText = "Practice",
                    onAction = {
                        viewModel.loadSurah(progressItem.surahId)
                    }
                )
            }
        }
    }
}

@Composable
fun ScheduleAyahItemCard(
    surahName: String,
    ayahNumber: Int,
    statusText: String,
    buttonText: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_item_${surahName}_$ayahNumber"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$surahName • Ayah $ayahNumber",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onAction,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = buttonText, fontSize = 12.sp)
            }
        }
    }
}
