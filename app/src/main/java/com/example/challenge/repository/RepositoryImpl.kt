package com.example.challenge.repository

import androidx.lifecycle.MutableLiveData
import com.example.challenge.api.common.ApiResponse
import com.example.challenge.api.common.scheduler.SchedulerProvider
import com.example.challenge.api.crimes.dto.CrimeModel
import com.example.challenge.api.crimes.service.CrimeDataService
import io.reactivex.disposables.CompositeDisposable

class RepositoryImpl constructor(
    private val crimeDataService: CrimeDataService,
    private val schedulerProvider: SchedulerProvider
): Repository {
    val crimesByAreaResponseLiveData = MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>>()
    val crimesBySpecificLocationResponseLiveData = MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>>()

    val compositeDisposable = CompositeDisposable()

    /**
     * Fetch crimes by custom area
     **/
    override fun getCrimesByArea(poly:String,date:String) {
        compositeDisposable.add(crimeDataService.getCrimesByCustomArea(poly,date)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {crimesByAreaResponseLiveData.value = ApiResponse.loading() }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    crimesByAreaResponseLiveData.value = ApiResponse.success(response = response.body())
                } else  {
                    try {
                        crimesByAreaResponseLiveData.value = ApiResponse.error(response.code())
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
    override fun getCrimesBySpecificLocation(date:String,lat:Double,lng:Double) {
        val service = crimeDataService.getCrimesByLocation(date,lat,lng)
        compositeDisposable.add(service
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {crimesBySpecificLocationResponseLiveData.value = ApiResponse.loading() }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    crimesBySpecificLocationResponseLiveData.value = ApiResponse.success(response = response.body())
                } else  {
                    try {
                        crimesBySpecificLocationResponseLiveData.value = ApiResponse.error(response.code())
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

    override fun getCrimesByAreaObservable(): MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>> {
        return crimesByAreaResponseLiveData
    }

    override fun getCrimesBySpecificLocationObservable(): MutableLiveData<ApiResponse<List<CrimeModel.CrimeData>>> {
        return crimesBySpecificLocationResponseLiveData
    }

    override fun getCompositeDisposableObject(): CompositeDisposable {
        return compositeDisposable
    }
}