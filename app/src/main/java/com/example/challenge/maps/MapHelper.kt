package com.example.challenge.maps

import com.google.maps.android.clustering.Cluster

object MapHelper {
    fun checkIfLocationsAreNotTheSame(
        cluster: Cluster<CrimeLocationItem>
    ): Boolean {
        var someLocationsNotTheSame = false
        val initLat = cluster.items.first()?.position?.latitude
        val initLong = cluster.items.first()?.position?.longitude
        cluster.items.forEach {
            if (initLat != it?.position?.latitude) {
                someLocationsNotTheSame = true
            }
            if (initLong != it?.position?.longitude) {
                someLocationsNotTheSame = true
            }
        }
        return someLocationsNotTheSame
    }
}