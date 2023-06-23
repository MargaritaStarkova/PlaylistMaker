package com.practicum.playlistmaker.search.domain.models

sealed class FetchResult(val data: List<TrackModel>? = null, val error: NetworkError? = null) {
    class Success(data: List<TrackModel>): FetchResult(data = data)
    class Error(error: NetworkError): FetchResult(error = error)
}
