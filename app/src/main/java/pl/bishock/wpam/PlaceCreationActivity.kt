package pl.bishock.wpam

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_place_creation.*
import kotlinx.android.synthetic.main.item_dialog_place_create.view.*
import pl.bishock.wpam.ModelClasses.Place


class PlaceCreationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var markerList: MutableList<Marker> = mutableListOf()
    private var newPlace:Boolean = true
    private lateinit var mPlace: Place
    private var mainTitle = ""
    private var mainDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newPlace = intent.getBooleanExtra("newPlace", true)
        if (newPlace) {
            mainTitle = intent.getStringExtra("title").toString()
            mainDescription = intent.getStringExtra("description").toString()
        }else{
            mPlace = intent.getSerializableExtra("place") as Place
            mainTitle = mPlace.title
            mainDescription = mPlace.description
            val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(firebaseUser!!)
                .child("Places")
                .child(mPlace.id).removeValue()

        }


        setContentView(R.layout.activity_place_creation)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        created_title.text = mainTitle

        base_layout.let {
            Snackbar.make(it,"Long press to add a marker!",Snackbar.LENGTH_INDEFINITE)
                .setAction("OK") {}
                .setActionTextColor(ContextCompat.getColor(this,R.color.dark_red))
                .show()
        }

        save_place.setOnClickListener {
            savePlace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (!newPlace){
            for (spot in mPlace.spotList){
                var marker = mMap.addMarker(
                    MarkerOptions().position(LatLng(spot.latitude.toDouble(),spot.longitude.toDouble())).title(spot.title)
                        .snippet(spot.title)
                )
                markerList.add(marker)
            }
        }

        mMap.setOnInfoWindowClickListener { marker ->
            markerList.remove(marker)
            marker.remove()
        }

        mMap.setOnMapLongClickListener { latLng ->
            showAlertDialog(latLng)
        }

    }

    private fun savePlace() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
        var userReference : DatabaseReference? = null
        var id :String = ""
        if (newPlace){
            userReference =
                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(firebaseUser!!)
                    .child("Places")
                    .push()

            id = userReference.key.toString()
        }else{


            id = mPlace.id
            userReference = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(firebaseUser!!)
                .child("Places")
                .child(id)

        }

        val hashMap = HashMap<String, Any>()
        hashMap["title"] = mainTitle
        hashMap["description"] = mainDescription
        hashMap["id"] = id
        userReference.updateChildren(hashMap)

        userReference = userReference.child("spotList")

        for (spot in markerList){
            val markerMap = HashMap<String, Any>()
            markerMap["title"] = spot.title
            markerMap["description"] = spot.snippet
            markerMap["longitude"] = spot.position.longitude.toString()
            markerMap["latitude"] = spot.position.latitude.toString()
            userReference.push().updateChildren(markerMap)

        }
        finish()
    }




    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView =
            LayoutInflater.from(this).inflate(R.layout.item_dialog_place_create, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create marker")
            .setView(placeFormView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.marker_title.text.toString().trim()
            val description = placeFormView.marker_description.text.toString().trim()
            if (title == "" || description == "") {
                Toast.makeText(
                    this,
                    "Spot must have non-empty title and description",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val marker =
                    mMap.addMarker(
                        MarkerOptions().position(latLng).title(getString(R.string.title_separation,mainTitle,title))
                            .snippet(getString(R.string.title_separation,mainDescription,description))
                    )
                mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                    override fun getInfoWindow(arg0: Marker?): View? {
                        return null
                    }

                    override fun getInfoContents(marker: Marker): View? {
                        val info = LinearLayout(this@PlaceCreationActivity)
                        info.orientation = LinearLayout.VERTICAL
                        val infoTitle = TextView(this@PlaceCreationActivity)
                        infoTitle.setTextColor(Color.BLACK)
                        infoTitle.gravity = Gravity.CENTER
                        infoTitle.setTypeface(null, Typeface.BOLD)
                        infoTitle.text = marker.title
                        val snippet = TextView(this@PlaceCreationActivity)
                        snippet.setTextColor(Color.GRAY)
                        snippet.text = marker.snippet
                        info.addView(infoTitle)
                        info.addView(snippet)
                        return info
                    }
                })

                markerList.add(marker)
                dialog.dismiss()
            }
        }
    }


}