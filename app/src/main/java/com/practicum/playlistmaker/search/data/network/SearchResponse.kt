package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.storage.models.TrackModelDto

class SearchResponse(val results: List<TrackModelDto>) : Response()