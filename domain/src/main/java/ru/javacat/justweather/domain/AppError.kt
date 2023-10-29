package ru.javacat.justweather.domain

sealed class AppError() : RuntimeException()
class ApiError(val code: Int, override val message: String) : AppError()
class DbError(override val message: String) : AppError()

object NetworkError : AppError()
object UnknownError : AppError()