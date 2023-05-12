package com.practicum.playlistmaker.search.data.network

interface INetworkClient {
    fun doRequest(query: String) : Response
}