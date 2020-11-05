package com.example.challenge.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import com.example.challenge.repository.Repository

open class CrimesViewModel @ViewModelInject constructor(
    val repository: Repository) : BaseViewModel() {

    val crimesByAreaObservable
        get() = repository.getCrimesByAreaObservable()

    val crimesBySpecificLocationObservable
        get() = repository.getCrimesBySpecificLocationObservable()

    init {
        compositeDisposable = repository.getCompositeDisposableObject()
    }

    /**
     * Fetch crimes by custom area
     **/
    fun getCrimesByArea(poly:String,date:String) {
        repository.getCrimesByArea(poly,date)
    }

    /**
     * Fetch crimes by specific location
     **/
    fun getCrimesBySpecificLocation(date:String,lat:Double,lng:Double) {
        repository.getCrimesBySpecificLocation(date,lat,lng)
    }
}