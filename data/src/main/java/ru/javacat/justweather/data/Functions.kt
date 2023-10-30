package ru.javacat.justweather.data

import retrofit2.Response
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.DbError
import ru.javacat.justweather.domain.NetworkError
import java.net.SocketException

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
        println("SOCKET_ERROR: $e")
        throw NetworkError
    } catch (e: Exception) {
        println("NETWORK_ERROR: $e")
        throw NetworkError
    }
    if (!response.isSuccessful) throw ApiError(response.code(), response.message())
    return response.body() ?: throw ApiError(0, "null body")
}