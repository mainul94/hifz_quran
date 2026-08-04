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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AiAssistantViewModel

@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("ai_assistant_screen")
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Speech Accuracy AI") },
                icon = { Icon(Icons.Default.Mic, contentDescription = null) },
                modifier = Modifier.testTag("tab_speech_analysis")
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Tajweed Chatbot") },
                icon = { Icon(Icons.Default.QuestionAnswer, contentDescription = null) },
                modifier = Modifier.testTag("tab_tajweed_chatbot")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTabIndex == 0) {
            SpeechAccuracyAnalysisTab(viewModel = viewModel)
        } else {
            TajweedChatbotTab(viewModel = viewModel)
        }
    }
}

@Composable
fun SpeechAccuracyAnalysisTab(viewModel: AiAssistantViewModel) {
    val isAnalyzing by viewModel.isAnalyzingSpeech.collectAsState()
    val analysisResult by viewModel.speechAnalysisResult.collectAsState()

    var expectedAyahText by remember {
        mutableStateOf("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
    }
    var recitedText by remember {
        mutableStateOf("بسم الله الرحمن الرحيم")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Recitation Speech Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Immediate corrective feedback on pronunciation & Tajweed using Gemini AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = expectedAyahText,
                onValueChange = { expectedAyahText = it },
                label = { Text("Original Ayah Arabic Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expected_ayah_input")
            )
        }

        item {
            OutlinedTextField(
                value = recitedText,
                onValueChange = { recitedText = it },
                label = { Text("Recited Text / Transcribed Sound") },
                placeholder = { Text("Type or record your recitation...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recited_text_input")
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.evaluateRecitation(expectedAyahText, recitedText)
                },
                enabled = !isAnalyzing && expectedAyahText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analyze_recitation_button")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyzing Speech with AI...")
                } else {
                    Icon(Icons.Default.Psychology, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Recitation Accuracy")
                }
            }
        }

        analysisResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analysis_result_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recitation Score",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (result.scorePercent >= 85) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.secondaryContainer
                                    )
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${result.scorePercent}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = result.feedbackText,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TajweedChatbotTab(viewModel: AiAssistantViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tajweed_chatbot_tab")
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { (sender, message) ->
                val isUser = sender == "You"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = sender,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask about Tajweed rules...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chatbot_input_field"),
                enabled = !isChatLoading
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val q = inputText
                    inputText = ""
                    viewModel.sendTajweedQuestion(q)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("send_question_button"),
                enabled = !isChatLoading && inputText.isNotBlank()
            ) {
                if (isChatLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
