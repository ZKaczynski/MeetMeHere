package pl.bishock.wpam

import android.content.Intent
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
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_place_creation.*
import kotlinx.android.synthetic.main.activity_place_view.*
import pl.bishock.wpam.ModelClasses.Place
import pl.bishock.wpam.ModelClasses.Spot
import pl.bishock.wpam.ModelClasses.Users

class PlaceViewActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mPlaces: ArrayList<Place>? = null
    private var visitId = ""
    private var visitRef: DatabaseReference? = null
    private var placeRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_view)
        setSupportActionBar(view_places_toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        view_places_toolbar.setNavigationOnClickListener {
            val intent = Intent(this@PlaceViewActivity, MainActivity::class.java)
            startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        }
        visitId = intent.getStringExtra("visit_id").toString()

        visitRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(visitId)

        placeRef = visitRef!!.child("Places")

        visitRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                view_places_username.text = user!!.username
                Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile)
                    .into(view_places_profile_image)
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        placeRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s0: DataSnapshot) {
                mPlaces = ArrayList<Place>()
                for (snapshot in s0.children) {
                    val place = snapshot.child("spotList")
                    val spotList = ArrayList<Spot>()
                    for (spotSnapshot in place.children) {
                        val spot = spotSnapshot.getValue(Spot::class.java)
                        spot?.let { spotList.add(it) }
                    }

                    val title: String =
                        snapshot.child("title").getValue(String::class.java).toString()
                    val description: String =
                        snapshot.child("description").getValue(String::class.java).toString()
                    val id: String =
                        snapshot.child("id").getValue(String::class.java).toString()


                    (mPlaces as ArrayList).add(Place(title, description, spotList,id))

                    val latLngBounds = LatLngBounds.Builder()
                    for (place in mPlaces!!) {
                        for (spot in place.spotList) {
                            val marker = LatLng(spot.latitude.toDouble(), spot.longitude.toDouble())
                            latLngBounds.include(marker)
                            googleMap.addMarker(
                                MarkerOptions().position(marker).title(spot.title)
                                    .snippet(spot.description)
                            )

                            //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.female))
                        }
                    }
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            latLngBounds.build(),
                            1000,
                            1000,
                            0
                        )
                    )
                    googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoWindow(arg0: Marker?): View? {
                            return null
                        }

                        override fun getInfoContents(marker: Marker): View? {
                            val info = LinearLayout(this@PlaceViewActivity)
                            info.orientation = LinearLayout.VERTICAL
                            val infoTitle = TextView(this@PlaceViewActivity)
                            infoTitle.setTextColor(Color.BLACK)
                            infoTitle.gravity = Gravity.CENTER
                            infoTitle.setTypeface(null, Typeface.BOLD)
                            infoTitle.text = marker.title
                            val snippet = TextView(this@PlaceViewActivity)
                            snippet.setTextColor(Color.GRAY)
                            snippet.text = marker.snippet
                            info.addView(infoTitle)
                            info.addView(snippet)
                            return info
                        }
                    })
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )


    }
}