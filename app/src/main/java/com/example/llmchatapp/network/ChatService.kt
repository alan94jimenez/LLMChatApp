package com.example.llmchatapp.network

import com.example.llmchatapp.ui.models.ChatMessage

/**
 * A standardized interface for interacting with different LLM chat services.
 * Each implementation of this interface will handle the specific request/response
 * format for a particular LLM provider (e.g., Gemini, OpenAI).
 */
interface ChatService {
    /**
     * Sends a list of messages to the LLM and returns the model's response.
     *
     * @param apiKey The user's API key for the service.
     * @param modelId The specific model to use for the chat completion.
     * @param messages The history of the conversation to be sent to the model.
     * @return A Result object containing either the successful ChatMessage response
     * or an Exception if the call fails.
     */
    suspend fun generateContent(
        apiKey: String,
        modelId: String,
        messages: List<ChatMessage>
    ): Result<ChatMessage>
}
