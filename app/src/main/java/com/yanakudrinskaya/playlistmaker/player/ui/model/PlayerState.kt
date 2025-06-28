package com.yanakudrinskaya.playlistmaker.player.ui.model

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val status: PlayStatus, val progress: String) {

    class Default : PlayerState(false, PlayStatus.PLAY, "00:00")

    class Prepared : PlayerState(true, PlayStatus.PLAY, "00:00")

    class Playing(progress: String) : PlayerState(true, PlayStatus.PAUSE, progress)

    class Paused(progress: String) : PlayerState(true, PlayStatus.PLAY, progress)
}