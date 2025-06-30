package com.yanakudrinskaya.playlistmaker.player.ui.model


sealed class PlayerState(val isPlayButtonEnabled: Boolean, val status: PlayStatus, val progress: String) {

    class Default(progress: String) : PlayerState(false, PlayStatus.PLAY, progress)

    class Prepared(progress: String) : PlayerState(true, PlayStatus.PLAY, progress)

    class Playing(progress: String) : PlayerState(true, PlayStatus.PAUSE, progress)

    class Paused(progress: String) : PlayerState(true, PlayStatus.PLAY, progress)

}