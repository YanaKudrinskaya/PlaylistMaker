package com.yanakudrinskaya.playlistmaker.sharing.domain

import android.content.Intent
import com.yanakudrinskaya.playlistmaker.sharing.domain.model.AgreementData
import com.yanakudrinskaya.playlistmaker.sharing.domain.model.ShareData
import com.yanakudrinskaya.playlistmaker.sharing.domain.model.SupportData

interface ExternalNavigator {
    fun navigateToShare(shareData: ShareData): Intent
    fun navigateToSupport(supportData: SupportData): Intent
    fun navigateToAgreement(agreementData: AgreementData): Intent
}