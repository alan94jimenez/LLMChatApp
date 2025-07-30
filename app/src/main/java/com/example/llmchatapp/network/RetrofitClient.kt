package com.example.llmchatapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A singleton object to provide configured Retrofit instances for API calls.
 */
object RetrofitClient {

    // Base URLs for the different APIs
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val OPENAI_BASE_URL = "https://api.openai.com/"
    private const val ANTHROPIC_BASE_URL = "https://api.anthropic.com/"
    private const val GROK_BASE_URL = "https://api.groq.com/"


    // An HttpLoggingInterceptor to log network requests and responses to Logcat.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // The OkHttpClient that will be used by Retrofit. We add our logger to it.
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * A lazily-initialized Retrofit instance for the Gemini API.
     */
    val gemini: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * A lazily-initialized Retrofit instance for the OpenAI API.
     */
    val openAI: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * A lazily-initialized Retrofit instance for the Anthropic API.
     */
    val anthropic: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ANTHROPIC_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * A lazily-initialized Retrofit instance for the Grok API.
     */
    val grok: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GROK_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
