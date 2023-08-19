package ru.javacat.justweather

import android.database.SQLException
import java.io.IOException

sealed class AppError() : RuntimeException()
class ApiError(val code: Int, override val message: String) : AppError()
class DbError(override val message: String) : AppError()

object NetworkError : AppError()
object UnknownError : AppError()