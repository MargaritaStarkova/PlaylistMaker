package com.practicum.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}

class SearchResponse(val results: ArrayList<TrackData>)