package com.example.llmchatapp.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.llmchatapp.data.Conversation
import com.example.llmchatapp.data.Llm
import com.example.llmchatapp.data.repository.ConversationRepository
import com.example.llmchatapp.security.SecureStorage

class NewChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConversationRepository
    private val secureStorage = SecureStorage(application)

    var selectedLlmProvider by mutableStateOf<Llm?>(null)
        private set
    var savedModels by mutableStateOf<List<String>>(emptyList())
        private set

    fun onProviderSelected(llm: Llm) {
        selectedLlmProvider = llm
        loadModelsForProvider(llm)
    }

    private fun loadModelsForProvider(llm: Llm) {
        val modelsString = secureStorage.get(llm.modelStorageKey)
        savedModels = if (modelsString.isNullOrBlank()) {
            emptyList()
        } else {
            modelsString.split(',')
        }
    }

    fun onBackToProviders() {
        selectedLlmProvider = null
        savedModels = emptyList()
    }

    fun createNewConversation(llmName: String, modelId: String): Conversation {
        return repository.createNewConversation(llmName, modelId)
    }
}

class NewChatViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewChatViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
