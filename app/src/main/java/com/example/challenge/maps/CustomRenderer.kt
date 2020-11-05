package com.example.challenge.maps

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class CustomRenderer<ClusterItem>(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<CrimeLocationItem>?
    ) :
        DefaultClusterRenderer<CrimeLocationItem>(context, map, clusterManager) {
        override fun shouldRenderAsCluster(cluster: Cluster<CrimeLocationItem>): Boolean {
            //start clustering if at least 2 items overlap
            //Change your logic here
            return cluster.size > 1
        }

        override fun onBeforeClusterRendered(cluster: Cluster<CrimeLocationItem>, markerOptions: MarkerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions)
            if (!MapHelper.checkIfLocationsAreNotTheSame(cluster)){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
        }
    }