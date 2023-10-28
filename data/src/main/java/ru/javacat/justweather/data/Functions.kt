package ru.javacat.justweather.util

import retrofit2.Response
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.DbError
import ru.javacat.justweather.NetworkError
import java.net.SocketException
import kotlin.Exception

suspend fun <T>dbQuery(query: suspend () -> T): T {
    return try {
        query()
    } catch (e: Exception) {
        throw  DbError(e.stackTraceToString())
    }
}

suspend fun <T> apiRequest(request: suspend () -> Response<T>): T{

    val response = try {
        request()
    } catch (e: SocketException) {
        throw NetworkError
    } catch (e: Exception) {
        throw NetworkError
    }
    if (!response.isSuccessful) throw ApiError(response.code(), response.message())
    return response.body() ?: throw ApiError(0, "null body")
}