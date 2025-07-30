package com.example.llmchatapp.ui.screens

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.llmchatapp.data.LlmProvider
import com.example.llmchatapp.ui.viewmodels.NewChatViewModel
import com.example.llmchatapp.ui.viewmodels.NewChatViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(
    navController: NavController,
    viewModel: NewChatViewModel = viewModel(
        factory = NewChatViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val selectedProvider = viewModel.selectedLlmProvider
    val savedModels = viewModel.savedModels

    if (selectedProvider != null) {
        BackHandler {
            viewModel.onBackToProviders()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (selectedProvider == null) "Select Provider" else "Select Model"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedProvider != null) {
                            viewModel.onBackToProviders()
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (selectedProvider == null) {
            // --- Step 1: Show LLM Provider List ---
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(LlmProvider.llms) { llm ->
                    ListItem(
                        headlineContent = { Text(llm.name) },
                        modifier = Modifier.clickable { viewModel.onProviderSelected(llm) }
                    )
                    Divider()
                }
            }
        } else {
            // --- Step 2: Show User-Saved Model List ---
            if (savedModels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No models saved for ${selectedProvider.name}. Add them in Settings.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    items(savedModels) { modelId ->
                        ListItem(
                            headlineContent = { Text(modelId) },
                            modifier = Modifier.clickable {
                                val newConversation = viewModel.createNewConversation(selectedProvider.name, modelId)
                                navController.navigate("chat_screen/${newConversation.id}") {
                                    popUpTo("chat_history_screen")
                                }
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
