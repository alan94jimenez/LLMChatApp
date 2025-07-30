package com.example.llmchatapp.network

import com.example.llmchatapp.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * A Retrofit service interface for defining API endpoints.
 */
interface ApiService {

    // --- Gemini ---
    @POST("v1beta/models/{modelId}:generateContent")
    suspend fun generateGeminiContent(
        @Path("modelId") modelId: String,
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>

    // --- OpenAI ---
    @POST("v1/chat/completions")
    suspend fun generateOpenAiContent(
        @Header("Authorization") authorization: String,
        @Body request: OpenAIRequest
    ): Response<OpenAIResponse>

    // --- Anthropic ---
    @POST("v1/messages")
    suspend fun generateAnthropicContent(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") apiVersion: String = "2023-06-01",
        @Body request: AnthropicRequest
    ): Response<AnthropicResponse>

    // --- Grok ---
    /**
     * Sends a chat message to the Grok API.
     * Note: Grok uses an OpenAI-compatible endpoint.
     * @param authorization The user's API key, formatted as "Bearer [API_KEY]".
     * @param request The request body, using the OpenAI format.
     * @return A Retrofit Response object wrapping the OpenAIResponse.
     */
    @POST("openai/v1/chat/completions")
    suspend fun generateGrokContent(
        @Header("Authorization") authorization: String,
        @Body request: OpenAIRequest
    ): Response<OpenAIResponse>
}
