package com.example.llmchatapp.data

import com.example.llmchatapp.ui.models.ChatMessage

/**
 * Represents a single conversation session.
 *
 * @param id A unique identifier for the conversation.
 * @param llmName The name of the LLM provider used in this conversation.
 * @param modelId The specific model ID used in this conversation (e.g., "gpt-4o").
 * @param title A title for the conversation, often the first user message.
 * @param messages The list of messages within this conversation.
 * @param timestamp The time the conversation was last updated.
 */
data class Conversation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val llmName: String,
    val modelId: String,
    var title: String,
    val messages: MutableList<ChatMessage> = mutableListOf(),
    var timestamp: Long = System.currentTimeMillis()
)
