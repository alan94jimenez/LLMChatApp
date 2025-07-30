package com.example.llmchatapp.network.services

import com.example.llmchatapp.network.ChatService
import com.example.llmchatapp.network.RetrofitClient
import com.example.llmchatapp.network.models.OpenAIMessage
import com.example.llmchatapp.network.models.OpenAIRequest
import com.example.llmchatapp.ui.models.ChatMessage
import com.example.llmchatapp.ui.models.Participant

/**
 * An implementation of the ChatService interface for the Grok API.
 * This service leverages the OpenAI-compatible endpoint provided by Grok.
 */
class GrokChatService : ChatService {
    override suspend fun generateContent(
        apiKey: String,
        modelId: String,
        messages: List<ChatMessage>
    ): Result<ChatMessage> {
        return try {
            // Grok uses the same request structure as OpenAI.
            val request = OpenAIRequest(
                model = modelId,
                messages = messages
                    .filter { it.participant != Participant.ERROR }
                    .map {
                        val role = if (it.participant == Participant.USER) "user" else "assistant"
                        OpenAIMessage(role = role, content = it.text)
                    }
            )

            // Call the specific Grok endpoint through our Retrofit client.
            val response = RetrofitClient.grok.generateGrokContent("Bearer $apiKey", request)

            if (response.isSuccessful) {
                // Grok also uses the same response structure as OpenAI.
                val modelResponse = response.body()?.choices?.firstOrNull()?.message?.content
                if (modelResponse != null) {
                    Result.success(ChatMessage(text = modelResponse, participant = Participant.MODEL))
                } else {
                    Result.failure(Exception("Received an empty but successful response from Grok."))
                }
            } else {
                Result.failure(Exception("Grok API Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
