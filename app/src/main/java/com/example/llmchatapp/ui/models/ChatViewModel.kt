package com.example.llmchatapp.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.llmchatapp.data.LlmProvider
import com.example.llmchatapp.data.repository.ConversationRepository
import com.example.llmchatapp.network.ChatService
import com.example.llmchatapp.network.services.AnthropicChatService
import com.example.llmchatapp.network.services.GeminiChatService
import com.example.llmchatapp.network.services.GrokChatService
import com.example.llmchatapp.network.services.OpenAiChatService
import com.example.llmchatapp.security.SecureStorage
import com.example.llmchatapp.ui.models.ChatMessage
import com.example.llmchatapp.ui.models.Participant
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application,
    private val conversationId: String
) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)
    private val repository = ConversationRepository

    /**
     * A map that holds an instance of the correct ChatService for each LLM provider.
     * This is where we add support for new LLMs.
     */
    private val chatServices: Map<String, ChatService> = mapOf(
        "Gemini" to GeminiChatService(),
        "OpenAI" to OpenAiChatService(),
        "Anthropic" to AnthropicChatService(),
        "Grok" to GrokChatService()
    )

    var uiState by mutableStateOf<ChatUiState>(ChatUiState.Loading)
        private set

    var userInput by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            repository.conversations.collect { conversationsMap ->
                val conversation = conversationsMap[conversationId]
                if (conversation == null) {
                    if (uiState !is ChatUiState.Error) uiState = ChatUiState.Error("Conversation not found.")
                    return@collect
                }

                val currentUiState = uiState
                if (currentUiState is ChatUiState.Loading) {
                    val llm = LlmProvider.llms.find { it.name == conversation.llmName }
                    val apiKey = llm?.let { secureStorage.get(it.apiKeyAlias) }
                    if (llm == null || apiKey.isNullOrBlank()) {
                        uiState = ChatUiState.Error("API Key for '${conversation.llmName}' not found.")
                        return@collect
                    }
                    uiState = ChatUiState.Success(conversation, apiKey, llm.name, conversation.modelId)
                } else if (currentUiState is ChatUiState.Success) {
                    uiState = currentUiState.copy(conversation = conversation)
                }
            }
        }
    }

    fun onUserInputChange(text: String) {
        userInput = text
    }

    fun sendMessage() {
        if (userInput.isBlank() || uiState !is ChatUiState.Success) return

        val userMessage = ChatMessage(text = userInput, participant = Participant.USER)
        repository.addMessageToConversation(conversationId, userMessage)
        userInput = ""
        triggerNetworkRequest()
    }

    private fun triggerNetworkRequest() {
        val currentState = uiState
        if (currentState !is ChatUiState.Success) return

        uiState = currentState.copy(isLoading = true)

        viewModelScope.launch {
            val service = chatServices[currentState.llmName]
            if (service == null) {
                val errorMessage = ChatMessage("Error: No chat implementation for ${currentState.llmName}.", participant = Participant.ERROR)
                repository.addMessageToConversation(conversationId, errorMessage)
            } else {
                val result = service.generateContent(
                    apiKey = currentState.apiKey,
                    modelId = currentState.modelId.trim(),
                    messages = currentState.conversation.messages
                )
                result.onSuccess { responseMessage ->
                    repository.addMessageToConversation(conversationId, responseMessage)
                }.onFailure { error ->
                    val errorMessage = ChatMessage("Error: ${error.message}", participant = Participant.ERROR)
                    repository.addMessageToConversation(conversationId, errorMessage)
                }
            }

            val finalState = uiState
            if (finalState is ChatUiState.Success) {
                uiState = finalState.copy(isLoading = false)
            }
        }
    }
}

sealed interface ChatUiState {
    object Loading : ChatUiState
    data class Error(val message: String) : ChatUiState
    data class Success(
        val conversation: com.example.llmchatapp.data.Conversation,
        val apiKey: String,
        val llmName: String,
        val modelId: String,
        val isLoading: Boolean = false
    ) : ChatUiState
}

class ChatViewModelFactory(
    private val application: Application,
    private val conversationId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(application, conversationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
