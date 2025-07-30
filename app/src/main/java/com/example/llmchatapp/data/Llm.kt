package com.example.llmchatapp.data

/**
 * Represents a single Large Language Model provider.
 * @param name The display name of the LLM (e.g., "Gemini").
 * @param apiKeyAlias A unique key used for storing the API key securely.
 * @param modelStorageKey A unique key for storing the user's custom list of models.
 */
data class Llm(
    val name: String,
    val apiKeyAlias: String,
    val modelStorageKey: String
)

/**
 * Provides a static list of supported LLMs in the application.
 * The hardcoded model lists have been removed.
 */
object LlmProvider {
    val llms = listOf(
        Llm("Gemini", "gemini_api_key", "gemini_models_list"),
        Llm("OpenAI", "openai_api_key", "openai_models_list"),
        Llm("Anthropic", "anthropic_api_key", "anthropic_models_list"),
        Llm("Grok", "grok_api_key", "grok_models_list")
    )
}
