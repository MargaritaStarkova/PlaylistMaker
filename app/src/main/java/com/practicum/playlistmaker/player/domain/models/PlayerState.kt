package com.practicum.playlistmaker.player.domain.models

sealed class PlayerState(val buttonState: Boolean, val progress: Int) {

    class Default : PlayerState(false, START_POSITION)
    class Prepared : PlayerState(true, START_POSITION)
    class Playing(progress: Int) : PlayerState(true, progress)
    class Paused(progress: Int) : PlayerState(true, progress)

    companion object {
        const val START_POSITION = 0
    }
}
