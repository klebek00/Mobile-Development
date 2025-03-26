package com.example.watertracker.passkey

sealed interface SignUpResult {
    data class  Success(val username: String): SignUpResult
    data object Cancelled: SignUpResult
    data object Failure: SignUpResult
}