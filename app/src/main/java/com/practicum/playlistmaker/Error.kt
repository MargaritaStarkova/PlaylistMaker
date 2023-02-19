package com.practicum.playlistmaker

sealed class Error
object Success : Error()
object SearchError : Error()
object ConnectionError : Error()
