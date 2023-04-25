package com.practicum.playlistmaker.presentation.presenters.search

import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.domain.models.NetworkError

interface SearchView {
    fun showSearchList(list: List<TrackModel>)
    fun showHistoryList(list: ArrayList<TrackModel>)
    fun showMessage(error: NetworkError)
    fun clearSearchText()
    fun hideKeyboard()
    fun showLoading()
    fun clearButtonVisibility(query: String?)
}