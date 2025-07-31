package com.example.llmchatapp.navigation

sealed class Screen(val route: String) {
    object ChatHistory : Screen("chat_history")
    object ApiKey      : Screen("api_key")
    object NewChat     : Screen("new_chat")

    // For screens that take arguments, define a helper
    object ChatDetail : Screen("chat_detail/{conversationId}") {
        private const val ARG_ID = "conversationId"
        fun createRoute(conversationId: String) = "chat_detail/$conversationId"
        fun getArgKey() = ARG_ID
    }
}
