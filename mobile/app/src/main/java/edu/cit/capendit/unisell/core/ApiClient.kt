package edu.cit.capendit.unisell.core

import android.content.Context
import android.content.SharedPreferences
import edu.cit.capendit.unisell.auth.AuthApi
import edu.cit.capendit.unisell.category.CategoryApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private const val PREFS_NAME = "UniSellPrefs"
    private const val TOKEN_KEY = "auth_token"


    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Attaches the stored token as Authorization: Bearer <token> on every outgoing request
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val token = getToken()
        val request = if (token != null) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        chain.proceed(request)
    }

    // Reads X-New-Token from every response and overwrites the stored token (30-min sliding window, FR-021)
    private val tokenRefreshInterceptor = okhttp3.Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val newToken = response.header("X-New-Token")
        if (newToken != null) {
            saveToken(newToken)
        }
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(tokenRefreshInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
        val categoryApi: CategoryApi = retrofit.create(CategoryApi::class.java)

}