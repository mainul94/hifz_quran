package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AnalyticsLeaderboardScreen
import com.example.ui.screens.MemorizeScreen
import com.example.ui.screens.RepetitionScheduleScreen
import com.example.ui.screens.VoiceNotesScreen
import com.example.ui.theme.HifzQuranTheme
import com.example.viewmodel.AiAssistantViewModel
import com.example.viewmodel.AnalyticsLeaderboardViewModel
import com.example.viewmodel.QuranViewModel
import com.example.viewmodel.VoiceNotesViewModel

import androidx.compose.material.icons.filled.Settings
import com.example.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HifzQuranTheme {
                MainAppScaffold()
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    val quranViewModel: QuranViewModel = viewModel()
    val aiAssistantViewModel: AiAssistantViewModel = viewModel()
    val voiceNotesViewModel: VoiceNotesViewModel = viewModel()
    val analyticsViewModel: AnalyticsLeaderboardViewModel = viewModel()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("bottom_navigation_bar")) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Book, contentDescription = null) },
                    label = { Text("Memorize") },
                    modifier = Modifier.testTag("nav_item_memorize")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    label = { Text("Schedule") },
                    modifier = Modifier.testTag("nav_item_schedule")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text("AI Assistant") },
                    modifier = Modifier.testTag("nav_item_ai_assistant")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Mic, contentDescription = null) },
                    label = { Text("Voice Notes") },
                    modifier = Modifier.testTag("nav_item_voice_notes")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    label = { Text("Leaderboard") },
                    modifier = Modifier.testTag("nav_item_leaderboard")
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> MemorizeScreen(
                viewModel = quranViewModel,
                modifier = Modifier.padding(innerPadding),
                onNavigateToSettings = { selectedTab = 5 }
            )
            1 -> RepetitionScheduleScreen(viewModel = quranViewModel, modifier = Modifier.padding(innerPadding))
            2 -> AiAssistantScreen(viewModel = aiAssistantViewModel, modifier = Modifier.padding(innerPadding))
            3 -> VoiceNotesScreen(viewModel = voiceNotesViewModel, modifier = Modifier.padding(innerPadding))
            4 -> AnalyticsLeaderboardScreen(viewModel = analyticsViewModel, modifier = Modifier.padding(innerPadding))
            5 -> SettingsScreen(viewModel = quranViewModel, modifier = Modifier.padding(innerPadding))
        }
    }
}

