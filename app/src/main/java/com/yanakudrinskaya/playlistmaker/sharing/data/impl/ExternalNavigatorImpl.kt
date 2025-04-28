package com.yanakudrinskaya.playlistmaker.sharing.data.impl

import android.content.Intent
import android.net.Uri
import com.yanakudrinskaya.playlistmaker.sharing.domain.ExternalNavigator
import com.yanakudrinskaya.playlistmaker.sharing.domain.model.AgreementData
import com.yanakudrinskaya.playlistmaker.sharing.domain.model.ShareData
import com.yanakudrinskaya.playlistmaker.sharing.domain.model.SupportData

class ExternalNavigatorImpl(
) : ExternalNavigator {

    override fun navigateToShare(shareData: ShareData): Intent {
        return Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareData.message)
            }
    }

    override fun navigateToSupport(supportData: SupportData): Intent {
        return  Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportData.email))
                putExtra(Intent.EXTRA_SUBJECT, arrayOf(supportData.subject))
                putExtra(Intent.EXTRA_TEXT, arrayOf(supportData.message))
            }
    }

    override fun navigateToAgreement(agreementData: AgreementData): Intent {
        val url = Uri.parse(agreementData.url)
        return Intent(Intent.ACTION_VIEW, url)
    }
}



