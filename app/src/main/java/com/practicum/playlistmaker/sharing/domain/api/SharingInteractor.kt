package com.practicum.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun share(text: String)
    fun openTerms()
    fun openSupport()
}
