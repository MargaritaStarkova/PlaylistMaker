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
        return "https://practicum.yandex.ru/android-developer/"
    }
    
    private fun getSupportEmailData(): EmailData {
        return EmailData(mail = "margo.ivi@yandex.ru")
    }
    
    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}