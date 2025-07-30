package com.example.llmchatapp.network.services

import com.example.llmchatapp.network.ChatService
import com.example.llmchatapp.network.RetrofitClient
import com.example.llmchatapp.network.models.Content
import com.example.llmchatapp.network.models.GeminiRequest
import com.example.llmchatapp.network.models.Part
import com.example.llmchatapp.ui.models.ChatMessage
import com.example.llmchatapp.ui.models.Participant

/**
 * An implementation of the ChatService interface for the Google Gemini API.
 */
class GeminiChatService : ChatService {
    override suspend fun generateContent(
        apiKey: String,
        modelId: String,
        messages: List<ChatMessage>
    ): Result<ChatMessage> {
        // Use a try-catch block to handle potential network errors gracefully.
        return try {
            val request = GeminiRequest(
                contents = messages
                    .filter { it.participant != Participant.ERROR }
                    .map { Content(parts = listOf(Part(it.text))) }
            )

            val response = RetrofitClient.gemini.generateGeminiContent(modelId, request, apiKey)

            if (response.isSuccessful) {
                val modelResponse = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (modelResponse != null) {
                    Result.success(ChatMessage(text = modelResponse, participant = Participant.MODEL))
                } else {
                    Result.failure(Exception("Received an empty but successful response."))
                }
            } else {
                Result.failure(Exception("API Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
