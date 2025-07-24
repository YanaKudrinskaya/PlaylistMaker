package com.yanakudrinskaya.playlistmaker.player.ui.model

sealed interface ToastState {
    data class Show(
        val trackAdded: Boolean,
        val message: String
    ) : ToastState
    object DontShow: ToastState
}
