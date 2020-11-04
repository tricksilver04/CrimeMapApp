package com.cartrack.challenge.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.cartrack.challenge.R
import com.cartrack.challenge.api.ApiCore
import com.cartrack.challenge.api.common.ApiResponse
import com.cartrack.challenge.api.common.Status
import com.cartrack.challenge.api.crimes.dto.CrimeModel
import com.cartrack.challenge.viewmodels.CrimesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMonthNow: String

    val crimesViewModel: CrimesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()
        crimesViewModel.crimesByAreaResponseLiveData.observe(this, Observer {
            handleCrimesResponse(it)
        });
        crimesViewModel.crimesBySpecificLocationResponseLiveData.observe(this, Observer {
            handleCrimesSpecificLocationResponse(it)
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        indeterminateBar.progressTintList = ColorStateList.valueOf(Color.RED)
        mMonthNow = "2020-02" //SimpleDateFormat("yyyy-MM").format(Date())
        btn_month_year_picker.text = mMonthNow
        btn_month_year_picker.setOnClickListener {

        }
    }

    private lateinit var clusterManager: ClusterManager<CrimeLocationItem>

    private fun handleCrimesResponse(apiResponse: ApiResponse<List<CrimeModel.CrimeData>>?) {
        apiResponse?.let {
            when(apiResponse.mStatus){
                Status.SUCCESS -> {
                    indeterminateBar.visibility = View.GONE
                    clusterManager.clearItems()
                    apiResponse.mResponse?.forEach { crime ->
                        val item = CrimeLocationItem(
                            lat = crime.location.latitude,
                            lng = crime.location.longitude,
                            title = crime.category,
                            snippet = ""
                        )
                        clusterManager.addItem(item)

                    }
                    clusterManager.onCameraIdle()
                    clusterManager.cluster()
                }
                Status.ERROR -> {
                    indeterminateBar.visibility = View.GONE
                    showToast("Please make the scope smaller, API can only handle items below 10,000")
                }
                Status.LOADING -> {
                    indeterminateBar.visibility = View.VISIBLE
                }
                Status.FAIL -> {
                    indeterminateBar.visibility = View.GONE
                }
            }
        }
    }

    fun handleCrimesSpecificLocationResponse(apiResponse: ApiResponse<List<CrimeModel.CrimeData>>?) {
        apiResponse?.let {
            when(apiResponse.mStatus){
                Status.SUCCESS -> {
                    indeterminateBar.visibility = View.GONE
                    val i = Intent(this,CrimeDataListActivity::class.java)
                    i.putParcelableArrayListExtra("crime_list", ArrayList(apiResponse?.mResponse!!))
                    startActivity(i)
                }
                Status.ERROR -> {
                    indeterminateBar.visibility = View.GONE
                    showToast("Please make the scope smaller, API can only handle items below 10,000")
                }
                Status.LOADING -> {
                    indeterminateBar.visibility = View.VISIBLE
                }
                Status.FAIL -> {
                    indeterminateBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker for user
        val loc = LatLng(51.503186, -0.126446)//LatLng(geo.lat, geo.lng)
        mMap.addMarker(MarkerOptions().position(loc).title("crimes here"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
        mMap.setMaxZoomPreference(30f)
        mMap.setOnCameraIdleListener {
            val northEast = mMap.projection.visibleRegion.latLngBounds.northeast
            val sounthWest = mMap.projection.visibleRegion.latLngBounds.southwest
            val poly = "${northEast.latitude},${northEast.longitude}:${northEast.latitude},${sounthWest.longitude}:${sounthWest.latitude},${sounthWest.longitude}:${sounthWest.latitude},${northEast.longitude}"

            ApiCore.client?.dispatcher()?.cancelAll()
            crimesViewModel.getCrimesByArea(poly,mMonthNow)
            clusterManager.onCameraIdle()
        }

        clusterManager = ClusterManager(this, mMap)

        clusterManager.renderer = CustomRenderer<CrimeLocationItem>(this, mMap, clusterManager)

        mMap.setOnMarkerClickListener { marker ->
            val crimeData = marker.tag as CrimeModel.CrimeData
            clusterManager.onMarkerClick(marker)
            crimesViewModel.getCrimesBySpecificLocation(mMonthNow, crimeData.location.latitude, crimeData.location.longitude)
            return@setOnMarkerClickListener true
        }

        clusterManager.setOnClusterClickListener {cluster ->
            if (!checkIfLocationsAreNotTheSame(cluster)){
                crimesViewModel.getCrimesBySpecificLocation(mMonthNow, cluster.items.first().position.latitude, cluster.items.first().position.longitude)
            }
            return@setOnClusterClickListener true
        }


    }

    private fun checkIfLocationsAreNotTheSame(
        cluster: Cluster<CrimeLocationItem>
    ): Boolean {
        var someLocationsNotTheSame = false
        val initLat = cluster.items.first()?.getPosition()?.latitude
        val initLong = cluster.items.first()?.getPosition()?.longitude
        cluster.items.forEach {
            if (initLat != it?.getPosition()?.latitude) {
                someLocationsNotTheSame = true
            }
            if (initLong != it?.getPosition()?.longitude) {
                someLocationsNotTheSame = true
            }
        }
        return someLocationsNotTheSame
    }

    inner class CrimeLocationItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) : ClusterItem {

        private val position: LatLng
        private val title: String
        private val snippet: String

        override fun getPosition(): LatLng {
            return position
        }

        override fun getTitle(): String? {
            return title
        }

        override fun getSnippet(): String? {
            return snippet
        }

        init {
            position = LatLng(lat, lng)
            this.title = title
            this.snippet = snippet
        }
    }

    inner class CustomRenderer<ClusterItem>(
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
            if (!checkIfLocationsAreNotTheSame(cluster)){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
        }
    }


}