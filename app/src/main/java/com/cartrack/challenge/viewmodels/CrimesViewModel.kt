package com.cartrack.challenge.viewmodels

import androidx.lifecycle.MutableLiveData
import com.cartrack.challenge.api.common.ApiResponse
import com.cartrack.challenge.api.common.scheduler.SchedulerProvider
import androidx.hilt.lifecycle.ViewModelInject
import com.cartrack.challenge.api.crimes.dto.CrimeModel
import com.cartrack.challenge.api.crimes.service.CrimeDataService

open class CrimesViewModel @ViewModelInject constructor(
    val crimeDataService: CrimeDataService,
    val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val crimesByAreaResponseLiveData = MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>>()
    val crimesBySpecificLocationResponseLiveData = MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>>()

    /**
     * Fetch crimes by custom area
     **/
    fun getCrimesByArea(poly:String,date:String) {
        compositeDisposable.add(crimeDataService.getCrimesByCustomArea(poly,date)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {crimesByAreaResponseLiveData.value = ApiResponse.loading() }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    crimesByAreaResponseLiveData.value = ApiResponse.success(response = response.body())
                } else  {
                    try {
                        crimesByAreaResponseLiveData.value = ApiResponse.error(response.errorBody()?.string())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        crimesByAreaResponseLiveData.value = ApiResponse.fail(e)
                    }
                }
            }, { t ->
                t.printStackTrace()
                crimesByAreaResponseLiveData.value = ApiResponse.fail(t)
            }))
    }

    /**
     * Fetch crimes by specific location
     **/
    fun getCrimesBySpecificLocation(date:String,lat:Double,lng:Double) {
        compositeDisposable.add(crimeDataService.getCrimesByLocation(date,lat,lng)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {crimesBySpecificLocationResponseLiveData.value = ApiResponse.loading() }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    crimesBySpecificLocationResponseLiveData.value = ApiResponse.success(response = response.body())
                } else  {
                    try {
                        crimesBySpecificLocationResponseLiveData.value = ApiResponse.error(response.errorBody()?.string())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        crimesBySpecificLocationResponseLiveData.value = ApiResponse.fail(e)
                    }
                }
            }, { t ->
                t.printStackTrace()
                crimesBySpecificLocationResponseLiveData.value = ApiResponse.fail(t)
            }))
    }
}