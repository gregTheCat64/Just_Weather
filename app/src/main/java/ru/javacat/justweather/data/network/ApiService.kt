package ru.javacat.justweather.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.javacat.justweather.API_KEY
import ru.javacat.justweather.data.network.response_models.WeatherResponse
import ru.javacat.justweather.domain.models.SearchLocation


//private const val name = "London"

//val url = "http://api.weatherapi.com/v1/current.json?key=$API_KEY&q=Moscow&aqi=no"

//private const val BASE_URL = "https://api.weatherapi.com/v1/"
//
//private val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//}
//
//private val okhttp = OkHttpClient.Builder()
//    .addInterceptor(logging)
//    .connectTimeout(1, TimeUnit.SECONDS)
//    .build()
//
//private val retrofit = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(BASE_URL)
//    .client(okhttp)
//    .build()

const val DAYS_COUNT = 3

interface ApiService{
    @GET("forecast.json?key=$API_KEY")
    suspend fun getByName (
        @Query ("q") name: String,
        @Query ("days") daysCount: Int = DAYS_COUNT,
        @Query ("aqi") aqi: String = "no",
        @Query ("alerts") alerts: String = "yes",
        @Query ("lang") lang:String = "ru"
    ): Response<WeatherResponse>

    @GET("search.json?key=$API_KEY")
    suspend fun findLocation(
        @Query("q") name: String
    ): Response<List<SearchLocation>>
}

//object Api {
//    val service: ApiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }
//}