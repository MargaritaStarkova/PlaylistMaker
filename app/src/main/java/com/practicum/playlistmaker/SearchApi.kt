package com.practicum.playlistmaker

import retrofit2.*
import retrofit2.http.*

interface SearchApi {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<SearchResponse>
}

class SearchResponse(val results: ArrayList<TrackData>)