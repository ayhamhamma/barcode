package com.plcoding.barcodescanner.remote

import com.plcoding.barcodescanner.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

object RetrofitInstance {

    val api: ApiServices by lazy {
        provideRetrofitInstance(Constants.BASE_URL).create(ApiServices::class.java)
    }

    private fun provideRetrofitInstance(baseUrl: String): Retrofit {

        val interceptor = provideInterceptor()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(30)) // Connection timeout
            .readTimeout(Duration.ofSeconds(30))    // Read timeout
            .writeTimeout(Duration.ofSeconds(30)) // Write timeout
            .addInterceptor(interceptor)
            .build()

        val retrofitInstance = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofitInstance
    }

    private fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}