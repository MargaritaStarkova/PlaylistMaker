package com.practicum.playlistmaker.search.data.network

interface INetworkClient {
    suspend fun doRequest(query: String) : Response
}