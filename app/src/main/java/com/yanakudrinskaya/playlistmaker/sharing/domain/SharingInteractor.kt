package com.yanakudrinskaya.playlistmaker.sharing.domain

import android.content.Intent

interface SharingInteractor {
    fun shareApp() : Intent
    fun openTerms() : Intent
    fun openSupport() : Intent

    fun getShareError(): String
    fun getUserAgreementError(): String
    fun getSupportError(): String
}