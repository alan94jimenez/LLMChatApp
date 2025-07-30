package com.example.llmchatapp.network.services

import com.example.llmchatapp.network.ChatService
import com.example.llmchatapp.network.RetrofitClient
import com.example.llmchatapp.network.models.OpenAIMessage
import com.example.llmchatapp.network.models.OpenAIRequest
import com.example.llmchatapp.ui.models.ChatMessage
import com.example.llmchatapp.ui.models.Participant

/**
 * An implementation of the ChatService interface for the OpenAI API.
 */
class OpenAiChatService : ChatService {
    override suspend fun generateContent(
        apiKey: String,
        modelId: String,
        messages: List<ChatMessage>
    ): Result<ChatMessage> {
        return try {
            val request = OpenAIRequest(
                model = modelId,
                messages = messages
                    .filter { it.participant != Participant.ERROR }
                    .map {
                        val role = if (it.participant == Participant.USER) "user" else "assistant"
                        OpenAIMessage(role = role, content = it.text)
                    }
            )

            val response = RetrofitClient.openAI.generateOpenAiContent("Bearer $apiKey", request)

            if (response.isSuccessful) {
                val modelResponse = response.body()?.choices?.firstOrNull()?.message?.content
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
