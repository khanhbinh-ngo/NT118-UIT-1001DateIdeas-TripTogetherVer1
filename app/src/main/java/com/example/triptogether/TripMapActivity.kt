package com.example.triptogether

import android.os.Bundle
import android.view.View
import com.example.triptogether.databinding.ActivityTripMapBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.utilities.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TripMapActivity : PlannerActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTripMapBinding

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapTitle.text =
            intent.getStringExtra("country") ?: getString(R.string.trip_location)

        if (Constants.MAPS_API_KEY.isBlank()) {
            binding.mapStatus.apply {
                visibility = View.VISIBLE
                setText(R.string.map_key_missing)
            }
            return
        }
        val fragment =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                ?: SupportMapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().replace(R.id.map, it).commitNow()
                }
        fragment.getMapAsync(this)
    }

    // Called when the map is ready to be used
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Get the location data from the intent
        val lat = intent.getDoubleExtra("tripLat", 0.0)
        val lng = intent.getDoubleExtra("tripLng", 0.0)
        val countryName = intent.getStringExtra("country") ?: "Trip location"

        if (
            !lat.isFinite() ||
                !lng.isFinite() ||
                lat !in -90.0..90.0 ||
                lng !in -180.0..180.0 ||
                (lat == 0.0 && lng == 0.0)
        ) {
            binding.mapStatus.visibility = View.VISIBLE
            return
        }
        // Create a LatLng object and show a pin on the map
        val latLng = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(latLng).title(countryName))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
    }
}
