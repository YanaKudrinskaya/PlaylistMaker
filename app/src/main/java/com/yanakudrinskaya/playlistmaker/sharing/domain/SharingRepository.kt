package com.yanakudrinskaya.playlistmaker.sharing.domain

interface SharingRepository {
    fun getShareMessage(): String
    fun getSupportEmail(): String
    fun getSupportSubject(): String
    fun getSupportMessage(): String
    fun getUserAgreementUrl(): String
    fun getShareError(): String
    fun getUserAgreementError(): String
    fun getSupportError(): String
}