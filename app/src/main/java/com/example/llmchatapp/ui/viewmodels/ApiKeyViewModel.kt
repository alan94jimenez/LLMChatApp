package com.example.llmchatapp.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.llmchatapp.data.Llm
import com.example.llmchatapp.data.LlmProvider
import com.example.llmchatapp.security.SecureStorage

class ApiKeyViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)
    private val maxModels = 3

    // --- UI State ---
    var selectedLlm by mutableStateOf(LlmProvider.llms.first())
        private set
    var apiKeyInput by mutableStateOf("")
        private set
    var newModelInput by mutableStateOf("")
        private set
    var dropdownExpanded by mutableStateOf(false)
        private set
    var isKeySaved by mutableStateOf(false)
        private set
    val savedModels = mutableStateListOf<String>()

    init {
        // When the ViewModel is created, load data for the default LLM.
        loadDataForSelectedLlm()
    }

    // --- User Intent Handlers ---

    fun onLlmSelected(llm: Llm) {
        selectedLlm = llm
        loadDataForSelectedLlm()
        closeDropdown()
    }

    fun onApiKeyChanged(newKey: String) {
        apiKeyInput = newKey
    }

    fun onNewModelChanged(newModel: String) {
        newModelInput = newModel
    }

    fun saveApiKey() {
        if (apiKeyInput.isNotBlank()) {
            secureStorage.save(selectedLlm.apiKeyAlias, apiKeyInput)
            updateKeySavedStatus()
        }
    }

    fun addModel() {
        if (newModelInput.isNotBlank() && savedModels.size < maxModels && !savedModels.contains(newModelInput)) {
            savedModels.add(newModelInput)
            saveModelsList()
            newModelInput = "" // Clear input field after adding
        }
    }

    fun removeModel(model: String) {
        savedModels.remove(model)
        saveModelsList()
    }

    fun onDropdownToggled() {
        dropdownExpanded = !dropdownExpanded
    }

    fun closeDropdown() {
        dropdownExpanded = false
    }

    // --- Private Helper Functions ---

    private fun loadDataForSelectedLlm() {
        // Load API Key
        apiKeyInput = secureStorage.get(selectedLlm.apiKeyAlias) ?: ""
        updateKeySavedStatus()

        // Load Models List
        savedModels.clear()
        val modelsString = secureStorage.get(selectedLlm.modelStorageKey)
        if (!modelsString.isNullOrBlank()) {
            savedModels.addAll(modelsString.split(','))
        }
    }

    private fun saveModelsList() {
        val modelsString = savedModels.joinToString(",")
        secureStorage.save(selectedLlm.modelStorageKey, modelsString)
    }

    private fun updateKeySavedStatus() {
        isKeySaved = !secureStorage.get(selectedLlm.apiKeyAlias).isNullOrBlank()
    }
}

class ApiKeyViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiKeyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApiKeyViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
