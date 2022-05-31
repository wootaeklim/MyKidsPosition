package com.wt.kids.mykidsposition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.wt.kids.mykidsposition.data.response.ResponseItemsData
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.service.JeffService
import com.wt.kids.mykidsposition.utils.LocationUtils
import com.wt.kids.mykidsposition.utils.Logger
import com.wt.kids.mykidsposition.view.adapter.PlaceListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.OnMapClickListener {
    private val logTag = this::class.java.simpleName
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startService()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startService()
            } else -> {
                // No location access granted.
            }
        }
    }

    private val placeListAdapter = PlaceListAdapter()
    private val viewModel: MainViewModel by viewModels()

    @Inject lateinit var logger: Logger
    @Inject lateinit var locationUtils: LocationUtils

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapView: MapView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomSheetContainer: View
    private lateinit var searchPlaceButton: FloatingActionButton
    private lateinit var editTextView: EditText
    private lateinit var searchButton: ImageView
    private lateinit var bottomSheetTitleText: TextView
    private lateinit var searchEditTextContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.logD(logTag, "onCreate")
        setContentView(R.layout.activity_main)
        initViews(savedInstanceState)

        if (verifyPermissions(this)) {
            startService()
        } else {
            requestLocationPermission()
        }
    }

    private fun initViews(savedInstanceState: Bundle?) {
        logger.logD(logTag, "initViews")
        mapView = findViewById(R.id.mapView)
        recyclerView = findViewById(R.id.recyclerView)
        bottomSheetContainer = findViewById(R.id.bottomSheetContainer)
        searchPlaceButton = findViewById(R.id.searchPlaceButton)
        searchPlaceButton.setOnClickListener {
            searchEditTextContainer.visibility = View.VISIBLE
        }
        editTextView = findViewById(R.id.editTextView)
        editTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                logger.logD(logTag, "beforeTextChanged : $text")
            }

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                logger.logD(logTag, "onTextChanged : $text")
            }

            override fun afterTextChanged(e: Editable) {
            }
        })
        searchButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            searchEditTextContainer.visibility = View.GONE
            viewModel.searchPlace(editTextView.text.toString())
        }

        bottomSheetContainer.visibility = View.GONE
        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        // onCreate 연결
        mapView.onCreate(savedInstanceState)

        recyclerView.adapter = placeListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.searchData.observe(this, Observer {
            bottomSheetContainer.visibility = View.VISIBLE
            bottomSheetTitleText.text = String.format(getString(R.string.str_result_count), it.total)
            updateMarker(it.items)
            placeListAdapter.submitList(it.items)
        })

        bottomSheetTitleText = findViewById(R.id.bottomSheetTitleTextView)
        searchEditTextContainer = findViewById(R.id.searchEditTextContainer)
    }

    private fun startService() {
        logger.logD(logTag, "startService")
        val intent = Intent(this, JeffService::class.java)
        startForegroundService(intent)
        // 맵 가져오기 -> onMapReady
        mapView.getMapAsync(this)
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun verifyPermissions(context: Context): Boolean {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun updateMarker(items: List<ResponseItemsData>) {
        items.forEachIndexed { index, place ->
            Marker().apply {
                val tm = Tm128(place.mapx.toDouble(), place.mapy.toDouble())
                position = tm.toLatLng()
                onClickListener = null
                map = naverMap
                tag = index + 1
                icon = MarkerIcons.BLACK
                iconTintColor = Color.RED
            }
        }
    }

    override fun onMapReady(map: NaverMap) {
        logger.logD(logTag, "onMapReady")
        naverMap = map
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onMapClick(p0: PointF, p1: LatLng) {
        editTextView.visibility = View.GONE
    }
}