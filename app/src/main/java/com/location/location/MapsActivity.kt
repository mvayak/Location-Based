package com.location.location

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.location.location.Extention.makeAlertDialog
import com.location.location.Extention.showToast
import com.location.location.Util.AppConst.PERMISSION_CODE
import com.location.location.Util.PermissionUtils
import com.location.location.adapter.ViewPagerAdapter
import com.location.location.databinding.ActivityMapsBinding
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {


    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var sheetBehavior: BottomSheetBehavior<View>? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationManager: LocationManager? = null
    private lateinit var mMap: GoogleMap
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var mLocation: Location? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        initView()
        viewClickListener()

    }

    private fun viewClickListener() {
        floatingNavigationButton.setOnClickListener {
            if (latitude != null && longitude != null) {

                mMap.let {
                    onMapReady(mMap)
                    val location = LatLng(latitude!!, longitude!!)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f))
                }
            } else {
                startLocationUpdate()
            }
        }
    }

    // Initialling View
    private fun initView() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Manage Bottom Sheet View
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet);
        binding.bottomSheet.viewPagerImages.adapter = ViewPagerAdapter(this)
        binding.bottomSheet.indicatorImages.setViewPager(binding.bottomSheet.viewPagerImages)
        binding.bottomSheet.viewPagerImages.currentItem = 0
        sheetBehavior!!.isFitToContents=true
        sheetBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    binding.imageViewUser.setImageResource(R.mipmap.ic_export)
                    binding.imageViewNotification.setImageResource(R.mipmap.ic_close)

                } else {
                    binding.imageViewUser.setImageResource(R.mipmap.ic_user)
                    binding.imageViewNotification.setImageResource(R.mipmap.ic_notification)
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        if (PermissionUtils.checkRunTimePermission(this, *permissions)) {
            checkCurrentLocation()
            mMap.isMyLocationEnabled = true
        } else {
            PermissionUtils.requestPermission(this, PERMISSION_CODE, *permissions)
        }

        val success = mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.google_maps_style
            )
        )

        if (latitude != null && longitude != null) {
            val location = LatLng(latitude!!, longitude!!)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    checkCurrentLocation()
                } else {
                    resources.getString(R.string.need_location_permission).showToast(this)
                }
                return
            }
        }
    }


    override fun onConnected(p0: Bundle?) {

        if (!PermissionUtils.checkRunTimePermission(this, *permissions))
            return

        if (mLocation == null) {
            startLocationUpdate()
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {

            if (latitude == null && longitude == null) {
                latitude = mLocation!!.latitude
                longitude = mLocation!!.longitude

            }

        } else {
            "Location not Detected".showToast(this)
        }
    }


    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient?.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(p0: Location?) {
        latitude = p0?.latitude
        longitude = p0?.longitude
       if(mMap!=null){
           onMapReady(mMap)
       }
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient?.disconnect()
        }
    }

    private fun checkCurrentLocation(): Boolean {
        if (!isLocationEnabled())
            this.makeAlertDialog(true, resources.getString(R.string.enable_location),
                resources.getString(R.string.location_msg), resources.getString(R.string.location_settings),
                DialogInterface.OnClickListener { p0, _ ->
                    p0?.dismiss()
                    val myIntent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }, resources.getString(R.string.cancel), DialogInterface.OnClickListener { p0, p1 -> p0?.dismiss() })
        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }



    // For Location Update
    private fun startLocationUpdate() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)

        if (PermissionUtils.checkRunTimePermission(this, *permissions)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest, this
            )
        }


    }
}
