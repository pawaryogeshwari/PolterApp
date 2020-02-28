package com.polter.mobipolter.activities.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.LogEntryDetailActivity
import com.polter.mobipolter.activities.model.LocationEntity
import com.polter.mobipolter.activities.model.LogStackEntity
import com.polter.mobipolter.activities.utility.Utilities
import kotlinx.android.synthetic.main.fragment_location.*


class LocationFragment : Fragment(),OnMapReadyCallback {

    var googleMap : GoogleMap?= null
    var logId : Int = 0

    var mMap: GoogleMap ?= null

    private var mPermissionsGranted = false

    private val PERMISSIONS_REQUEST_CODE = 202

    var mGoogleApiClient : GoogleApiClient ?= null
    internal lateinit var mLastLocation: Location
    internal lateinit var mLocationResult: LocationRequest
    internal lateinit var mLocationCallback: LocationCallback
    internal var mCurrLocationMarker: Marker? = null
    internal lateinit var mLocationRequest: LocationRequest
    internal var mFusedLocationClient: FusedLocationProviderClient? = null

    var updatedLocationLat : Double = 0.0
    var updatedLocationLong : Double = 0.0
    var currentLocationLat : Double = 0.0
    var currentLocationLong : Double = 0.0

    var locationEntity : LocationEntity ? = null
    var locationLogTitle : String ?= null
    var mContext : Context ? = null
    var isFragVisible = false
    var isAttached = false
    var currentTab : String ?= null
    var loglist : List<LogStackEntity> ? = null

    private val mRequiredPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)

    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = LocationFragment::class.java.simpleName
        fun newInstance() = LocationFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView =  inflater.inflate(R.layout.fragment_location, container,
                false)


        mapFragment = childFragmentManager.findFragmentById(R.id.fallasMap) as SupportMapFragment?


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        /*currentTab = (this.activity as LogEntryDetailActivity).currentTabSelected
        if (currentTab != null && currentTab.equals(getString(R.string.tab_location)) && isFragVisible){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = hasPermissions(activity!!, mRequiredPermissions)
                if (!mPermissionsGranted) {
                    requestPermissions(mRequiredPermissions, PERMISSIONS_REQUEST_CODE)

                } else {
                    mapFragment?.getMapAsync(this)
                }
            } else {
                mapFragment?.getMapAsync(this)

            }

        }*/


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context

        isAttached = true


    }


    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        isFragVisible = visible

        if(activity != null)


        if (isFragVisible && context!= null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = hasPermissions(context!!, mRequiredPermissions)
                if (!mPermissionsGranted) {
                    requestPermissions(mRequiredPermissions, PERMISSIONS_REQUEST_CODE)

                }else{
                    mapFragment?.getMapAsync(this)
                }
            } else {
                mapFragment?.getMapAsync(this)

            }
        }


    }


    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logId = (this.activity as LogEntryDetailActivity).logID
        locationLogTitle = (this.activity as LogEntryDetailActivity).stackNR

        currentTab = (this.activity as LogEntryDetailActivity).currentTabSelected
        if(currentTab!= null && currentTab.equals(getString(R.string.tab_location))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = hasPermissions(context!!, mRequiredPermissions)
                if (!mPermissionsGranted) {
                    requestPermissions(mRequiredPermissions, PERMISSIONS_REQUEST_CODE)

                }else{
                    mapFragment?.getMapAsync(this)
                }
            } else {
                mapFragment?.getMapAsync(this)

            }
        }
       // locationListener = this
        mLocationCallback = LocationCallback()

        imgNavigate.setOnClickListener {
            if(currentLocationLat != updatedLocationLat && currentLocationLong != updatedLocationLong){

             val uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+currentLocationLat+","+currentLocationLong+"&daddr="+updatedLocationLat+","+updatedLocationLong
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(Intent.createChooser(intent, "Select an application"))
            }
        }

        txtUseCurrentLocation.setOnClickListener {

            if(mMap!!.myLocation != null){


            val latitude = mMap!!.myLocation.latitude
            val longtitude = mMap!!.myLocation.longitude
            edtLatitude.setText(latitude.toString())
            edtLongtitude.setText(longtitude.toString())
            val markerOptions = MarkerOptions()
                    .position(LatLng(latitude,
                            longtitude ))

            updatedLocationLat = latitude
            updatedLocationLong = longtitude

            val place = LatLng(latitude,
                    longtitude)
            mMap!!.addMarker(markerOptions.position(place).title(((activity as LogEntryDetailActivity)).mLogStackEntity?.logBasicEntity?.stackNR))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(place))

            }
        }

       /* edtLatitude.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(etValue: Editable?) {

                updatedLocationLat = Utilities.inputNumberValidation(etValue.toString())
                updatedLocationLong = Utilities.inputNumberValidation(edtLongtitude.text.toString())
                val latlong = LatLng(updatedLocationLat,updatedLocationLong)

                if(mMap != null)
                {
                    mMap!!.clear();

                    val mp = MarkerOptions()

                    mp.position(latlong);

                    mp.title("current position");

                    mMap!!.addMarker(mp);

                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16f));
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(etValue: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }
        })

        edtLongtitude.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(etValue: Editable?) {

                updatedLocationLong = Utilities.inputNumberValidation(etValue.toString())
                updatedLocationLat = Utilities.inputNumberValidation(edtLatitude.text.toString())
                val latlong = LatLng(updatedLocationLat,updatedLocationLong)


                if(mMap != null){
                    mMap!!.clear()

                    val mp = MarkerOptions()

                    mp.position(latlong);

                    mp.title("current position");

                    mMap?.addMarker(mp);

                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16f));
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(etValue: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }
        })*/


    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        if(hasPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)))
        mMap?.isMyLocationEnabled = true
        startLocationUpdates()
        registerLocationListner()
        locationEntity =  ((activity as LogEntryDetailActivity)).viewModel.findLocationTabById(logId)

        loglist = ((activity as LogEntryDetailActivity)).logList
        for (logStackEntity: LogStackEntity in loglist!!) {
            val locationEntitys = logStackEntity.logLocationEntity
            if(locationEntitys != null){
                val latitude = locationEntitys?.latitude
                val longtitude = locationEntitys?.longtitude
                // edtLatitude.setText(latitude.toString())
                //  edtLongtitude.setText(longtitude.toString())
                val markerOptions = MarkerOptions()
                        .position(LatLng(latitude!!,
                                longtitude!! ))

                val place = LatLng(latitude,
                        longtitude)
                mMap!!.addMarker(markerOptions.position(place).title(locationEntitys.locationLogTitle))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(place))
            }

        }
    }



    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions)
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false

        return true
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionsGranted = true
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment?.getMapAsync(this)
                } else {
                    try {

                        var showRationale = false
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            showRationale = shouldShowRequestPermissionRationale(permissions[0])
                        }
                        if (!showRationale) {
                            // user also CHECKED "never ask again"
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            //  val dialog = permissionPopup()
                            //  dialog.show()

                        } else {
                            // finish()
                            // user did NOT check "never ask again"
                            // this is a good place to explain the user
                            // why you need the permission and ask if he wants
                            // to accept it (the rationale)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // finish()
                    }


                    // mPermissionsGranted = false;
                    //  finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                // Check the result of each permission granted
            }
        }
    }

    override fun onStart() {
        super.onStart()
       // startLocationUpdates()
    }

    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(5000)
            setFastestInterval(5000)
        }

        // initialize location setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(activity!!)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listener

    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListner() {
        // initialize location callback object
       mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // 4. add permission if android version is greater then 23
        if(hasPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))) {
            LocationServices.getFusedLocationProviderClient(activity!!).requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        }
    }

    //
    private fun onLocationChanged(location: Location) {
      //  mMap?.clear();

        /*if(locationEntity != null){
            updatedLocationLat = location.latitude
            updatedLocationLong = location.longitude
            edtLatitude.setText((updatedLocationLat.toString()))
            edtLongtitude.setText(updatedLocationLong.toString())
        }else{
            currentLocationLat = location.latitude
            currentLocationLong = location.longitude
            edtLatitude.setText((currentLocationLat.toString()))
            edtLongtitude.setText(currentLocationLong.toString())
        }*/
        currentLocationLat = location.latitude
        currentLocationLong = location.longitude


        try{

            if(locationEntity != null){

                updatedLocationLat = locationEntity!!.latitude
                updatedLocationLong = locationEntity!!.longtitude
                edtLatitude.setText(updatedLocationLat.toString())
                edtLongtitude.setText(updatedLocationLong.toString())
                edtLatitude.addTextChangedListener(latitudeEditor)
                edtLongtitude.addTextChangedListener(longtitudeEditor)
                //  mMap?.clear();

                val mp = MarkerOptions()

                mp.position(LatLng(updatedLocationLat, updatedLocationLong));

                mp.title(((activity as LogEntryDetailActivity)).mLogStackEntity?.logBasicEntity?.stackNR)

                mMap?.addMarker(mp);
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(updatedLocationLat, updatedLocationLong), 5f));


            }else{
                edtLatitude.setText((currentLocationLat.toString()))
                edtLongtitude.setText(currentLocationLong.toString())
                edtLatitude.addTextChangedListener(latitudeEditor)
                edtLongtitude.addTextChangedListener(longtitudeEditor)

                // startLocationUpdates()
                val location = LocationEntity(logId,((activity as LogEntryDetailActivity)).mLogStackEntity?.logBasicEntity?.stackNR,currentLocationLat,currentLocationLong,0)
                ((activity as LogEntryDetailActivity)).viewModel.insertLocationTabDetail(location)
                val mp = MarkerOptions()

                mp.position(LatLng(currentLocationLat, currentLocationLong))

                mp.title("current position");

                mMap?.addMarker(mp);

                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(currentLocationLat, currentLocationLong), 5f))

            }

        }catch(e: java.lang.Exception){
            e.printStackTrace()
        }




      //  edtLatitude.setText((currentLocationLat.toString()))
      //  edtLongtitude.setText(currentLocationLong.toString())


        if(activity != null)
        LocationServices.getFusedLocationProviderClient(activity!!).removeLocationUpdates(mLocationCallback)

    }



    fun saveDataIntoDatabase(latitude: Double,longtitude: Double){

        val locationEntity = LocationEntity(logId,((activity as LogEntryDetailActivity)).mLogStackEntity?.logBasicEntity?.stackNR,
                latitude,
                longtitude,0)
        ((activity as LogEntryDetailActivity)).viewModel.updateLocationTabDetail(locationEntity)
        ((activity as LogEntryDetailActivity)).viewModel.updateLogLocationEntityById(locationEntity,locationEntity.locationLogDetailID)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /*if(locationEntity != null){
            saveDataIntoDatabase(updatedLocationLat,updatedLocationLong)
        }else{
            saveDataIntoDatabase(currentLocationLat,currentLocationLong)
        }*/
        saveDataIntoDatabase(Utilities.inputNumberValidation(edtLatitude.text.toString())
                ,Utilities.inputNumberValidation(edtLongtitude.text.toString()))


    }

    override fun onStop() {
       /* if(locationEntity != null){
            saveDataIntoDatabase(updatedLocationLat,updatedLocationLong)
        }else{
            saveDataIntoDatabase(currentLocationLat,currentLocationLong)
        }*/
        saveDataIntoDatabase(Utilities.inputNumberValidation(edtLatitude.text.toString())
                            ,Utilities.inputNumberValidation(edtLongtitude.text.toString()))
        super.onStop()

    }


    private val latitudeEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


        }

        override fun afterTextChanged(etValue: Editable?) {

            updatedLocationLat = Utilities.inputNumberValidation(etValue.toString())
            updatedLocationLong = Utilities.inputNumberValidation(edtLongtitude.text.toString())
            val latlong = LatLng(updatedLocationLat,updatedLocationLong)

            if(mMap != null)
            {
                mMap!!.clear();

                val mp = MarkerOptions()

                mp.position(latlong);

                mp.title(((activity as LogEntryDetailActivity)).mLogStackEntity?.logBasicEntity?.stackNR)

                mMap!!.addMarker(mp);

                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16f));
            }

        }
    }

    private val longtitudeEditor = object : TextWatcher {


        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


        }

        override fun afterTextChanged(etValue: Editable?) {

            updatedLocationLat = Utilities.inputNumberValidation(edtLatitude.text.toString())
            updatedLocationLong = Utilities.inputNumberValidation(etValue.toString())
            val latlong = LatLng(updatedLocationLat,updatedLocationLong)

            if(mMap != null)
            {
                mMap!!.clear();

                val mp = MarkerOptions()

                mp.position(latlong);

                mp.title(((activity as LogEntryDetailActivity)).mLogStackEntity?.logBasicEntity?.stackNR)

                mMap!!.addMarker(mp);

                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16f));
            }

        }
    }

}// Required empty public constructor




