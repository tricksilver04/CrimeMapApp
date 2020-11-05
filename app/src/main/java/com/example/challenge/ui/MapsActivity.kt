package com.example.challenge.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.challenge.R
import com.example.challenge.api.ApiCore
import com.example.challenge.api.common.ApiResponse
import com.example.challenge.api.common.Status
import com.example.challenge.api.crimes.dto.CrimeModel
import com.example.challenge.databinding.ActivityMapsBinding
import com.example.challenge.maps.CrimeLocationItem
import com.example.challenge.maps.CustomRenderer
import com.example.challenge.maps.MapHelper
import com.example.challenge.viewmodels.CrimesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.whiteelephant.monthpicker.MonthPickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMonthNow: String
    private lateinit var mBinding: ActivityMapsBinding

    private var mClusterManager: ClusterManager<CrimeLocationItem>? = null
    private val mCrimesViewModel: CrimesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        supportActionBar?.hide()
        setupViewModels()
        setupMapFragment()
        setupMonthYearPicker()
    }

    private fun setupMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupViewModels() {
        mCrimesViewModel.crimesByAreaObservable.observe(this, Observer {
            handleCrimesResponse(it)
        });
        mCrimesViewModel.crimesBySpecificLocationObservable.observe(this, Observer {
            handleCrimesSpecificLocationResponse(it)
        })
    }

    private fun addHeatMap(latLngs: List<LatLng?>?) {
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        val provider = HeatmapTileProvider.Builder()
            .data(latLngs)
            .build()

        // Add a tile overlay to the map, using the heat map tile provider.
        val overlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
        overlay.isVisible = true
    }

    private fun setupMonthYearPicker() {
        mMonthNow = SimpleDateFormat("yyyy-MM").format(Date())
        mBinding.btnMonthYearPicker.text = mMonthNow
        mBinding.btnMonthYearPicker.setOnClickListener {
            val parts = mMonthNow.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val builder = MonthPickerDialog.Builder(this,
                MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                    mMonthNow = "$selectedYear-${(selectedMonth + 1)}"
                    mBinding.btnMonthYearPicker.text = mMonthNow
                    processCrimeDataByAreaRequest()
                }, year, month - 1
            )
            builder
                .setMinYear(2018)
                .setActivatedYear(year)
                .setMaxYear(year)
                .setTitle("Select month")
                .build()
                .show();
        }
    }


    private fun handleCrimesResponse(apiResponse: ApiResponse<List<CrimeModel.CrimeData>>?) {
        apiResponse?.let {
            when(apiResponse.mStatus){
                Status.SUCCESS -> {
                    mBinding.indeterminateBar.visibility = View.GONE
                    mClusterManager?.clearItems()
                    //var latLngs: ArrayList<LatLng?> = ArrayList()
                    apiResponse.mResponse?.forEach { crime ->
                        val item = CrimeLocationItem(
                            lat = crime.location.latitude,
                            lng = crime.location.longitude,
                            title = crime.category,
                            snippet = ""
                        )
                        mClusterManager?.addItem(item)
                        //latLngs.add(LatLng(crime.location.latitude,crime.location.longitude))
                    }
                    //addHeatMap(latLngs)
                    mClusterManager?.onCameraIdle()
                    mClusterManager?.cluster()
                }
                else -> processOtherStatus(apiResponse)
            }
        }
    }

    fun handleCrimesSpecificLocationResponse(apiResponse: ApiResponse<List<CrimeModel.CrimeData>>?) {
        apiResponse?.let {
            when(apiResponse.mStatus){
                Status.SUCCESS -> {
                    mBinding.indeterminateBar.visibility = View.GONE
                    apiResponse?.mResponse?.let {
                        val i = Intent(this,CrimeDataListActivity::class.java)
                        i.putParcelableArrayListExtra("crime_list", ArrayList<CrimeModel.CrimeData>(it))
                        startActivity(i)
                    }
                }
                else -> processOtherStatus(apiResponse)
            }
        }
    }

    private fun processOtherStatus(apiResponse: ApiResponse<List<CrimeModel.CrimeData>>) {
        when(apiResponse.mStatus){
            Status.ERROR -> {
                mBinding.indeterminateBar.visibility = View.GONE
                when(apiResponse.mError){
                    503 -> showToast("Please make the scope smaller, API can only handle items below 10,000")
                    404 -> showToast("No data found for $mMonthNow")
                    else -> showToast("Something went wrong...")
                }
            }
            Status.LOADING -> {
                mBinding.indeterminateBar.visibility = View.VISIBLE
            }
            Status.FAIL -> {
                mBinding.indeterminateBar.visibility = View.GONE
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupGoogleMap(googleMap)
        setupClusterManager()
    }

    private fun setupGoogleMap(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker for user
        val loc = LatLng(51.503186, -0.126446)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
        mMap.setMaxZoomPreference(30f)
        mMap.setOnCameraIdleListener {
            processCrimeDataByAreaRequest()
        }
    }

    private fun setupClusterManager() {
        mClusterManager = ClusterManager(this, mMap)

        mClusterManager?.renderer = CustomRenderer<CrimeLocationItem>(this, mMap, mClusterManager)

        mClusterManager?.setOnClusterClickListener { cluster ->
            if (!MapHelper.checkIfLocationsAreNotTheSame(cluster)) {
                mCrimesViewModel.getCrimesBySpecificLocation(
                    mMonthNow,
                    cluster.items.first().position.latitude,
                    cluster.items.first().position.longitude
                )
            }
            return@setOnClusterClickListener true
        }

        mClusterManager?.setOnClusterItemClickListener { crimeData ->
            mCrimesViewModel.getCrimesBySpecificLocation(
                mMonthNow,
                crimeData.position.latitude,
                crimeData.position.longitude
            )
            return@setOnClusterItemClickListener true
        }
    }

    private fun processCrimeDataByAreaRequest() {
        val northEast = mMap.projection.visibleRegion.latLngBounds.northeast
        val sounthWest = mMap.projection.visibleRegion.latLngBounds.southwest
        val poly = "${northEast.latitude},${northEast.longitude}:${northEast.latitude},${sounthWest.longitude}:${sounthWest.latitude},${sounthWest.longitude}:${sounthWest.latitude},${northEast.longitude}"

        ApiCore.client?.dispatcher()?.cancelAll()
        mCrimesViewModel.getCrimesByArea(poly, mMonthNow)
        mClusterManager?.onCameraIdle()
    }

}