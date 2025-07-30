package com.example.llmchatapp.network.models

// --- Data classes for Gemini API Request ---
data class GeminiRequest(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(val text: String)

// --- Data classes for Gemini API Response ---
data class GeminiResponse(
    val candidates: List<Candidate>?,
    val error: GeminiError?
)
data class Candidate(val content: Content)
data class GeminiError(val code: Int, val message: String, val status: String)


// --- Data classes for OpenAI API Request ---
data class OpenAIRequest(val model: String, val messages: List<OpenAIMessage>)
data class OpenAIMessage(val role: String, val content: String)


// --- Data classes for OpenAI API Response ---
data class OpenAIResponse(
    val choices: List<OpenAIChoice>?,
    val error: OpenAIError?
)
data class OpenAIChoice(val message: OpenAIMessage)
data class OpenAIError(val message: String, val type: String)


// --- Data classes for Anthropic API Request ---

/**
 * The main request body sent to the Anthropic API.
 * @param model The specific model to use (e.g., "claude-3-opus-20240229").
 * @param messages A list of message objects, representing the conversation history.
 * @param max_tokens The maximum number of tokens to generate.
 */
data class AnthropicRequest(
    val model: String,
    val messages: List<AnthropicMessage>,
    val max_tokens: Int = 2048 // A sensible default
)

/**
 * Represents a single message in the Anthropic conversation format.
 * @param role The role of the message sender ("user" or "assistant").
 * @param content The text content of the message.
 */
data class AnthropicMessage(val role: String, val content: String)


// --- Data classes for Anthropic API Response ---

/**
 * The top-level response from the Anthropic API.
 * @param content A list of content blocks. For text responses, we'll use the first one.
 * @param error An optional error object if the request failed.
 */
data class AnthropicResponse(
    val content: List<AnthropicContentBlock>?,
    val error: AnthropicError?
)

/**
 * A block of content in the Anthropic response.
 * @param type The type of content, which should be "text".
 * @param text The actual text response from the model.
 */
data class AnthropicContentBlock(val type: String, val text: String)

/**
 * Represents an error returned by the Anthropic API.
 * @param type The type of error.
 * @param message A developer-facing error message.
 */
data class AnthropicError(val type: String, val message: String)
