package com.example.llmchatapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.llmchatapp.data.Conversation
import com.example.llmchatapp.ui.viewmodels.ChatHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatHistoryScreen(
    navController: NavController,
    viewModel: ChatHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var deleteAction by remember { mutableStateOf<() -> Unit>({}) }

    Scaffold(
        topBar = {
            if (uiState.isMultiSelectMode) {
                MultiSelectTopAppBar(
                    selectedCount = uiState.selectedConversationIds.size,
                    onClearSelection = { viewModel.clearSelection() },
                    onSelectAll = { viewModel.toggleSelectAll() },
                    onDelete = {
                        deleteAction = { viewModel.deleteSelectedConversations() }
                        showDeleteConfirmation = true
                    }
                )
            } else {
                DefaultTopAppBar(
                    navController = navController,
                    onDeleteAll = {
                        deleteAction = { viewModel.deleteAllConversations() }
                        showDeleteConfirmation = true
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.ChatDetail.createRoute("new_chat_screen") }) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
            }
        }
    ) { paddingValues ->
        if (uiState.conversations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No conversations yet. Start a new one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(uiState.conversations, key = { it.id }) { conversation ->
                    val isSelected = uiState.selectedConversationIds.contains(conversation.id)
                    ConversationListItem(
                        conversation = conversation,
                        isSelected = isSelected,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                if (uiState.isMultiSelectMode) {
                                    viewModel.toggleConversationSelection(conversation.id)
                                } else {
                                    navController.navigate(Screen.ChatDetail.createRoute("chat_screen/${conversation.id}")
                                }
                            },
                            onLongClick = {
                                viewModel.enterMultiSelectMode(conversation.id)
                            }
                        )
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                deleteAction()
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(navController: NavController, onDeleteAll: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("Conversations") },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Settings and More")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            navController.navigate(Screen.ChatDetail.createRoute("api_key_screen")
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete All Chats") },
                        onClick = {
                            onDeleteAll()
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectTopAppBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = { Text("$selectedCount Selected") },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Clear Selection")
            }
        },
        actions = {
            TextButton(onClick = onSelectAll) {
                Text("Select All")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Selected")
            }
        }
    )
}

@Composable
fun ConversationListItem(
    conversation: Conversation,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(Icons.Outlined.Circle, contentDescription = "Not Selected", tint = MaterialTheme.colorScheme.outline)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Model: ${conversation.modelId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatter.format(Date(conversation.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete the selected conversation(s)? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
