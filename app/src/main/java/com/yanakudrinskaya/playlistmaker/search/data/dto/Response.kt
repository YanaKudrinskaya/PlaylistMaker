package com.yanakudrinskaya.playlistmaker.search.data.dto

enum class ResponseStatus {
    SUCCESS,          // Успешный запрос (200)
    BAD_REQUEST,      // Неправильный запрос (400)
    NO_INTERNET,      // Нет интернета
    SERVER_ERROR,     // Ошибка сервера (500)
    UNKNOWN_ERROR     // Неизвестная ошибка
}

open class Response {
    var status: ResponseStatus = ResponseStatus.UNKNOWN_ERROR
}