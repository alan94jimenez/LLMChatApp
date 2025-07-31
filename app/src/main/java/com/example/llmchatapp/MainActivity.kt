package com.example.llmchatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.llmchatapp.navigation.Screen
import com.example.llmchatapp.ui.screens.ApiKeyScreen
import com.example.llmchatapp.ui.screens.ChatHistoryScreen
import com.example.llmchatapp.ui.screens.ChatScreen
import com.example.llmchatapp.ui.screens.NewChatScreen
import com.example.llmchatapp.ui.theme.LLMChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LLMChatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ChatHistory.route
    ) {
        composable(Screen.ChatHistory.route) {
            ChatHistoryScreen(navController)
        }
        composable(Screen.ApiKey.route) {
            ApiKeyScreen(navController)
        }
        composable(Screen.NewChat.route) {
            NewChatScreen(navController)
        }
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val conversationId = backStackEntry
                .arguments
                ?.getString(Screen.ChatDetail.getArgKey())
                .orEmpty()

            ChatScreen(
                navController = navController,
                conversationId = conversationId
            )
        }
    }
}
