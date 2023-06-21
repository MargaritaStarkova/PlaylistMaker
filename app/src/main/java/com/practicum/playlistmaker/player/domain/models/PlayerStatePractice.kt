package com.practicum.playlistmaker.player.domain.models

sealed class PlayerStatePractice(
    val isPlayButtonEnabled: Boolean,
    val playButtonText: String,
    val progress: Int
) {
    class Default : PlayerStatePractice(true, "PLAY", 0)
    class Prepared : PlayerStatePractice(true, "PLAY", 0)
    class Playing(progress: Int) : PlayerStatePractice(true, "PAUSE", progress)
    class Paused(progress: Int) : PlayerStatePractice(true, "PLAY", progress)
}
