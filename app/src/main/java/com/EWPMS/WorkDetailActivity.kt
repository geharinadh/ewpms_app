package com.EWPMS

import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import com.EWPMS.databinding.ActivityLoginScreenBinding
import com.EWPMS.databinding.ActivityWorkDetailBinding
import com.EWPMS.databinding.FragmentMyWorksBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class WorkDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var binding: ActivityWorkDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityWorkDetailBinding.inflate(layoutInflater);
        setContentView(binding.root)

        mapView = findViewById(R.id.small_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        tab_click_listeners()

        onclick_listeners()
    }

    private fun onclick_listeners() {
        binding.backIconLayout.setOnClickListener{
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun tab_click_listeners() {
        binding.workDetailCardview.setOnClickListener {
            binding.workDetailCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.sky_blue_dark))
            binding.workDetailTv.setTextColor(resources.getColor(R.color.white))

            binding.milestoneCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.milestoneTextview.setTextColor(applicationContext.resources.getColor(R.color.black))

            binding.presentPhotosCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.presentPhotosTextview.setTextColor(resources.getColor(R.color.black))

            binding.milestonesLayout.visibility= View.GONE
            binding.presentPhotosLayout.visibility= View.GONE
            binding.workDetailLayout.visibility= View.VISIBLE

        }

        binding.milestoneCardview.setOnClickListener {
            binding.milestoneCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.sky_blue_dark))
            binding.milestoneTextview.setTextColor(resources.getColor(R.color.white))

            binding.workDetailCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.workDetailTv.setTextColor(applicationContext.resources.getColor(R.color.black))

            binding.presentPhotosCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.presentPhotosTextview.setTextColor(resources.getColor(R.color.black))

            binding.workDetailLayout.visibility= View.GONE
            binding.presentPhotosLayout.visibility= View.GONE
            binding.milestonesLayout.visibility= View.VISIBLE
        }

        binding.presentPhotosCardview.setOnClickListener {
            binding.presentPhotosCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.sky_blue_dark))
            binding.presentPhotosTextview.setTextColor(resources.getColor(R.color.white))

            binding.workDetailCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.workDetailTv.setTextColor(applicationContext.resources.getColor(R.color.black))

            binding.milestoneCardview.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sky_blue))
            binding.milestoneTextview.setTextColor(resources.getColor(R.color.black))

            binding.workDetailLayout.visibility= View.GONE
            binding.milestonesLayout.visibility= View.GONE
            binding.presentPhotosLayout.visibility= View.VISIBLE
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.7749, -122.4194), 10f))
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
}