package com.example.llmchatapp.data.repository

import com.example.llmchatapp.data.Conversation
import com.example.llmchatapp.ui.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * A repository for managing conversation data.
 *
 * This implementation stores conversations in-memory. In a real-world application,
 * this would be backed by a persistent data source like a Room database.
 */
object ConversationRepository {

    private val _conversations = MutableStateFlow<Map<String, Conversation>>(emptyMap())
    val conversations: StateFlow<Map<String, Conversation>> = _conversations.asStateFlow()

    fun getConversation(id: String): Conversation? {
        return _conversations.value[id]
    }

    /**
     * Creates a new conversation with a specific LLM provider and model.
     * @param llmName The name of the provider (e.g., "OpenAI").
     * @param modelId The specific model to use (e.g., "gpt-4o").
     * @return The newly created Conversation object.
     */
    fun createNewConversation(llmName: String, modelId: String): Conversation {
        val newConversation = Conversation(
            llmName = llmName,
            modelId = modelId,
            title = "New Chat with $modelId"
        )
        _conversations.update { it + (newConversation.id to newConversation) }
        return newConversation
    }

    fun addMessageToConversation(id: String, message: ChatMessage) {
        _conversations.update { currentConversations ->
            val conversation = currentConversations[id]
            if (conversation != null) {
                // If it's the first user message, set it as the title.
                val newTitle = if (conversation.messages.isEmpty() && message.participant == com.example.llmchatapp.ui.models.Participant.USER) {
                    message.text
                } else {
                    conversation.title
                }

                // Create a new conversation object with the updated message list.
                val updatedConversation = conversation.copy(
                    title = newTitle,
                    messages = (conversation.messages + message).toMutableList(),
                    timestamp = System.currentTimeMillis()
                )
                currentConversations + (id to updatedConversation)
            } else {
                currentConversations
            }
        }
    }

    fun deleteConversation(id: String) {
        _conversations.update { it - id }
    }
}
