package ru.javacat.justweather.ui

sealed interface LoadingState {
    object Load: LoadingState

    object Success: LoadingState

    object InputError: LoadingState

    object NetworkError: LoadingState
}