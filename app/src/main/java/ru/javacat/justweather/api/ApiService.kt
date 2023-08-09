package ru.javacat.justweather.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.javacat.justweather.response_models.Weather


//private const val name = "London"

//val url = "http://api.weatherapi.com/v1/current.json?key=$API_KEY&q=Moscow&aqi=no"

private const val BASE_URL = "https://api.weatherapi.com/v1/"

private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface ApiService{
    @GET("current.json?key=$API_KEY")
    suspend fun getByName (@Query ("q") name: String): Response<Weather>
}

object Api {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}