package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun share(text: String) {
        externalNavigator.share(text)
    }
    
    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }
    
    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }
    
    private fun getSupportEmailData(): EmailData {
        return EmailData(mail = SUPPORT_EMAIL)
    }
    
    private fun getTermsLink(): String {
        return TERMS_LINK
    }
    
    companion object {
        private const val SUPPORT_EMAIL = "margo.ivi@yandex.ru"
        private const val TERMS_LINK = "https://yandex.ru/legal/practicum_offer/"
    }
}