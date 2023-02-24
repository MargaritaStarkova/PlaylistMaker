package com.practicum.api

data class TrackData(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackData

        if (trackId != other.trackId) return false

        return true
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}