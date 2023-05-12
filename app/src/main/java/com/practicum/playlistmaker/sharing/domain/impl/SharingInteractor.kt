package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.IExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.ISharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractor(
    private val externalNavigator: IExternalNavigator,
) : ISharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }
    
    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }
    
    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }
    
    private fun getShareAppLink(): String {
        return APP_LINK
    }
    
    private fun getSupportEmailData(): EmailData {
        return EmailData(mail = SUPPORT_EMAIL)
    }
    
    private fun getTermsLink(): String {
        return TERMS_LINK
    }
    
    companion object {
        private const val APP_LINK = "https://practicum.yandex.ru/android-developer/"
        private const val SUPPORT_EMAIL = "margo.ivi@yandex.ru"
        private const val TERMS_LINK = "https://yandex.ru/legal/practicum_offer/"
    }
}