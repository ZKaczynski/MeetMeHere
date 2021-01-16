package pl.bishock.wpam

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_place_creation.*
import pl.bishock.wpam.ModelClasses.Place

class PlaceEditActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mPlace: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_edit)

        mPlace = intent.getSerializableExtra("place") as Place
        setSupportActionBar(create_place)
        supportActionBar?.title = mPlace.title

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLngBounds = LatLngBounds.Builder()
        for (spot in mPlace.spotList){
            val marker = LatLng( spot.latitude.toDouble(),spot.longitude.toDouble())
            latLngBounds.include(marker)
            mMap.addMarker(MarkerOptions().position(marker).title(spot.title).snippet(spot.description))

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(),1000,1000,0))
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val info = LinearLayout(this@PlaceEditActivity)
                info.orientation = LinearLayout.VERTICAL
                val infoTitle = TextView(this@PlaceEditActivity)
                infoTitle.setTextColor(Color.BLACK)
                infoTitle.gravity = Gravity.CENTER
                infoTitle.setTypeface(null, Typeface.BOLD)
                infoTitle.text = marker.title
                val snippet = TextView(this@PlaceEditActivity)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(infoTitle)
                info.addView(snippet)
                return info
            }
        })
    }
}