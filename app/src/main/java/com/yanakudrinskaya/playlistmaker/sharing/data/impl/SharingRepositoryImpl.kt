package com.yanakudrinskaya.playlistmaker.sharing.data.impl

import android.content.Context
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(private val context: Context) :  SharingRepository
{
    override fun getShareMessage(): String = context.getString(R.string.share_message)
    override fun getSupportEmail(): String = context.getString(R.string.support_message_email)
    override fun getSupportSubject(): String = context.getString(R.string.support_message_subject)
    override fun getSupportMessage(): String = context.getString(R.string.support_message)
    override fun getUserAgreementUrl(): String = context.getString(R.string.agreement_address)
    override fun getShareError(): String = context.getString(R.string.share_toast)
    override fun getUserAgreementError(): String = context.getString(R.string.agreement_toast)
    override fun getSupportError(): String = context.getString(R.string.support_toast)

}