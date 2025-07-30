package com.example.llmchatapp.ui.models

/**
 * Represents a single message in the chat UI.
 *
 * @param text The content of the message.
 * @param participant The sender of the message (user, model, or an error).
 * @param isPending True if the message is waiting for a response, false otherwise.
 */
data class ChatMessage(
    val text: String,
    val participant: Participant = Participant.USER,
    val isPending: Boolean = false
)

/**
 * An enum to identify the sender of a chat message.
 */
enum class Participant {
    USER,
    MODEL,
    ERROR
}
