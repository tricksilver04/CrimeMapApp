package com.cartrack.challenge.api.crimes.service

import com.cartrack.challenge.api.crimes.dto.CrimeModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrimeDataService {
    @GET("/api/crimes-street/all-crime")
    fun getCrimesByCustomArea(
        @Query("poly",encoded = true) poly: String,
        @Query("date") date: String
    ): Observable<Response<List<CrimeModel.CrimeData>>>

    @GET("/api/crimes-at-location")
    fun getCrimesByLocation(
        @Query("date") date:String,
        @Query("lat") lat:Double,
        @Query("lng") lng:Double
    ): Observable<Response<List<CrimeModel.CrimeData>>>
}

