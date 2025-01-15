package com.deeplinknow

sealed class DLNError : Exception() {
    object NotInitialized : DLNError()
    object InvalidUrl : DLNError()
    object ServerError : DLNError()
    object ClipboardAccessDenied : DLNError()
} 