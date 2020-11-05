package com.example.challenge.repository

import androidx.lifecycle.MutableLiveData
import com.example.challenge.api.common.ApiResponse
import com.example.challenge.api.crimes.dto.CrimeModel
import io.reactivex.disposables.CompositeDisposable

interface Repository {

    fun getCrimesByArea(poly:String,date:String)

    fun getCrimesBySpecificLocation(date:String,lat:Double,lng:Double)

    fun getCrimesByAreaObservable(): MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>>

    fun getCrimesBySpecificLocationObservable(): MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>>

    fun getCompositeDisposableObject(): CompositeDisposable
}