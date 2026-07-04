package com.example.dreamweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.dreamweaver.data.model.Dream
import com.example.dreamweaver.data.model.availableMoods
import com.example.dreamweaver.data.model.moodEmoji
import com.example.dreamweaver.ui.DreamViewModel
import com.example.dreamweaver.ui.state.DreamFilter
import com.example.dreamweaver.ui.state.DreamSort
import com.example.dreamweaver.util.DreamPrompts
import com.example.dreamweaver.util.ShakeListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamListScreen(viewModel: DreamViewModel) {
    val state by viewModel.state.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DreamWeaver \uD83C\uDF19") },
                actions = {
                    IconButton(onClick = { viewModel.loadDailyInsight() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "ახალი დღის შთაგონება")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "მენიუ")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("ყველა სიზმარი") },
                            onClick = { viewModel.setFilter(DreamFilter.ALL); menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("მხოლოდ ღამისეული (Lucid)") },
                            onClick = { viewModel.setFilter(DreamFilter.LUCID_ONLY); menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("სორტირება: ახლები ჯერ") },
                            onClick = { viewModel.setSort(DreamSort.NEWEST); menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("სორტირება: ძველები ჯერ") },
                            onClick = { viewModel.setSort(DreamSort.OLDEST); menuExpanded = false }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "ახალი სიზმრის დამატება")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            InsightBanner(insight = state.dailyInsight, isLoading = state.isLoadingInsight)

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.displayedDreams.isEmpty()) {
                    Text(
                        text = if (state.filter == DreamFilter.LUCID_ONLY)
                            "ღამისეული (lucid) სიზმარი ჯერ არ გაქვთ ჩაწერილი"
                        else "სიზმრის დღიური ჯერ ცარიელია — დააჭირეთ + ღილაკს",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.displayedDreams, key = { it.id }) { dream ->
                            DreamCard(dream = dream, onDelete = { viewModel.deleteDream(dream) })
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddDreamSheet(
            onDismiss = { showAddSheet = false },
            onSave = { title, description, mood, isLucid ->
                viewModel.addDream(title, description, mood, isLucid)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun InsightBanner(insight: String?, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("\u2728 დღის შთაგონება", style = MaterialTheme.typography.labelMedium)
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp).height(20.dp))
            } else {
                Text(
                    text = insight ?: "დააჭირეთ განახლების ღილაკს",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DreamCard(dream: Dream, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${moodEmoji(dream.mood)}  ${dream.title}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "წაშლა")
                }
            }
            if (dream.description.isNotBlank()) {
                Text(
                    text = dream.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDate(dream.timestamp),
                    style = MaterialTheme.typography.labelMedium
                )
                if (dream.isLucid) {
                    AssistChip(onClick = {}, label = { Text("ღამისეული") })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDreamSheet(
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, mood: String, isLucid: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf(availableMoods.first()) }
    var isLucid by remember { mutableStateOf(false) }
    var shakeHintText by remember { mutableStateOf("\uD83D\uDCA1 შეანჯღრიეთ ტელეფონი შთაგონებისთვის") }

    // NOVEL FEATURE: while this sheet is open, listen for a shake gesture.
    // Shaking the phone appends a random writing prompt to the description.
    ShakeListener {
        val prompt = DreamPrompts.random()
        description = if (description.isBlank()) prompt else "$description\n$prompt"
        shakeHintText = "\uD83C\uDF1F შთაგონება დაემატა!"
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("ახალი სიზმარი", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("სათაური") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("აღწერა") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Text(
                text = shakeHintText,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text("განწყობა", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableMoods.forEach { m ->
                    FilterChip(
                        selected = mood == m,
                        onClick = { mood = m },
                        label = { Text(moodEmoji(m)) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ღამისეული (Lucid) სიზმარი იყო?")
                Switch(checked = isLucid, onCheckedChange = { isLucid = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("გაუქმება") }
                TextButton(onClick = { onSave(title, description, mood, isLucid) }) { Text("შენახვა") }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
