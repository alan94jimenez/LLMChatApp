package com.example.llmchatapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.llmchatapp.data.Conversation
import com.example.llmchatapp.data.repository.ConversationRepository
import kotlinx.coroutines.flow.*

/**
 * ViewModel for the ChatHistoryScreen.
 *
 * This ViewModel exposes a sorted list of conversations from the repository
 * and manages the state for multi-selection and deletion of conversations.
 */
class ChatHistoryViewModel : ViewModel() {

    private val repository = ConversationRepository

    // Private state for managing selection
    private val _uiState = MutableStateFlow(ChatHistoryUiState())
    val uiState: StateFlow<ChatHistoryUiState> = _uiState.asStateFlow()

    init {
        // Observe the repository and update the local state
        repository.conversations
            .map { it.values.sortedByDescending { conv -> conv.timestamp } }
            .onEach { conversations ->
                _uiState.update { it.copy(conversations = conversations) }
            }
            .launchIn(viewModelScope)
    }

    // --- User Intent Handlers ---

    /**
     * Toggles the selection state of a single conversation.
     * If not in multi-select mode, it enters multi-select mode.
     */
    fun toggleConversationSelection(conversationId: String) {
        _uiState.update { currentState ->
            val selectedIds = currentState.selectedConversationIds.toMutableSet()
            if (selectedIds.contains(conversationId)) {
                selectedIds.remove(conversationId)
            } else {
                selectedIds.add(conversationId)
            }
            // If the last item is deselected, exit multi-select mode.
            val isMultiSelectMode = selectedIds.isNotEmpty()
            currentState.copy(
                selectedConversationIds = selectedIds,
                isMultiSelectMode = isMultiSelectMode
            )
        }
    }

    /**
     * Enters multi-select mode, typically triggered by a long press.
     */
    fun enterMultiSelectMode(initialConversationId: String) {
        _uiState.update {
            it.copy(
                isMultiSelectMode = true,
                selectedConversationIds = setOf(initialConversationId)
            )
        }
    }

    /**
     * Exits multi-select mode and clears all selections.
     */
    fun clearSelection() {
        _uiState.update {
            it.copy(
                isMultiSelectMode = false,
                selectedConversationIds = emptySet()
            )
        }
    }

    /**
     * Selects all conversations if not all are selected, otherwise clears selection.
     */
    fun toggleSelectAll() {
        _uiState.update { currentState ->
            val allIds = currentState.conversations.map { it.id }.toSet()
            val allSelected = currentState.selectedConversationIds == allIds
            currentState.copy(
                selectedConversationIds = if (allSelected) emptySet() else allIds
            )
        }
    }

    /**
     * Deletes all currently selected conversations.
     */
    fun deleteSelectedConversations() {
        _uiState.value.selectedConversationIds.forEach { id ->
            repository.deleteConversation(id)
        }
        clearSelection() // Exit multi-select mode after deletion
    }

    /**
     * Deletes all conversations from the repository.
     */
    fun deleteAllConversations() {
        _uiState.value.conversations.forEach { conversation ->
            repository.deleteConversation(conversation.id)
        }
        clearSelection()
    }
}

/**
 * Represents the UI state for the ChatHistoryScreen.
 * @param conversations The list of all conversations.
 * @param isMultiSelectMode True if the user is in selection mode, false otherwise.
 * @param selectedConversationIds A set of IDs for the currently selected conversations.
 */
data class ChatHistoryUiState(
    val conversations: List<Conversation> = emptyList(),
    val isMultiSelectMode: Boolean = false,
    val selectedConversationIds: Set<String> = emptySet()
)
