package com.example.llmchatapp.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.llmchatapp.data.LlmProvider
import com.example.llmchatapp.ui.viewmodels.ApiKeyViewModel
import com.example.llmchatapp.ui.viewmodels.ApiKeyViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyScreen(
    navController: NavController,
    viewModel: ApiKeyViewModel = viewModel(
        factory = ApiKeyViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val selectedLlm = viewModel.selectedLlm
    val apiKeyInput = viewModel.apiKeyInput
    val newModelInput = viewModel.newModelInput
    val dropdownExpanded = viewModel.dropdownExpanded
    val savedModels = viewModel.savedModels

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text("LLM Settings", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            // --- LLM Provider Selector ---
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { viewModel.onDropdownToggled() }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = selectedLlm.name,
                    onValueChange = {},
                    label = { Text("LLM Provider") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { viewModel.closeDropdown() },
                ) {
                    LlmProvider.llms.forEach { llm ->
                        DropdownMenuItem(
                            text = { Text(llm.name) },
                            onClick = { viewModel.onLlmSelected(llm) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // --- API Key Management ---
            Text("API Key", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { viewModel.onApiKeyChanged(it) },
                label = { Text("Your API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.saveApiKey() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save API Key")
            }
            Spacer(modifier = Modifier.height(32.dp))

            // --- Custom Model Management ---
            Text("Manage Models", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            // Input for adding a new model
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newModelInput,
                    onValueChange = { viewModel.onNewModelChanged(it) },
                    label = { Text("Add Model ID") },
                    modifier = Modifier.weight(1f),
                    enabled = savedModels.size < 3
                )
                IconButton(onClick = { viewModel.addModel() }, enabled = savedModels.size < 3) {
                    Icon(Icons.Default.Add, contentDescription = "Add Model")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // List of saved models
        items(savedModels) { model ->
            SavedModelItem(modelName = model, onRemove = { viewModel.removeModel(model) })
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
fun SavedModelItem(modelName: String, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = modelName, modifier = Modifier.weight(1f))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove Model")
            }
        }
    }
}
