package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun share(text: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmail: EmailData)
}