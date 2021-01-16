package pl.bishock.wpam.Fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_places.view.*
import kotlinx.android.synthetic.main.item_dialog_place_create.view.*
import pl.bishock.wpam.AdapterClasses.PlaceAdapter
import pl.bishock.wpam.ModelClasses.Place
import pl.bishock.wpam.ModelClasses.Spot
import pl.bishock.wpam.PlaceCreationActivity
import pl.bishock.wpam.PlaceEditActivity
import pl.bishock.wpam.R
import kotlin.collections.ArrayList

class MyPlacesFragment : Fragment() {

    private var placeAdapter: PlaceAdapter? = null
    private var mPlaces: ArrayList<Place>? = null
    private var recyclerView: RecyclerView? = null
    private var gMap: GoogleMap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_places, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_spots_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        view.places_fab.setOnClickListener {
            val placeFormView =
                LayoutInflater.from(context).inflate(R.layout.item_dialog_place_create, null)
            val dialog = context?.let { it1 ->
                AlertDialog.Builder(it1)
                    .setTitle("Create new place!")
                    .setView(placeFormView)
                    .setPositiveButton("Save", null)
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                val title = placeFormView.marker_title.text.toString().trim()
                val description = placeFormView.marker_description.text.toString().trim()
                if (title == "" || description == "") {
                    Toast.makeText(
                        context,
                        "Place must have non-empty title and description",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(context, PlaceCreationActivity::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("description", description)
                    startActivity(intent)

                    dialog.dismiss()
                }
            }

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        updatePlaces()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val info = LinearLayout(context)
                info.orientation = LinearLayout.VERTICAL
                val infoTitle = TextView(context)
                infoTitle.setTextColor(Color.BLACK)
                infoTitle.gravity = Gravity.CENTER
                infoTitle.setTypeface(null, Typeface.BOLD)
                infoTitle.text = marker.title
                val snippet = TextView(context)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(infoTitle)
                info.addView(snippet)
                return info
            }
        })
    }

    private fun updatePlaces() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser)
                .child("Places")
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s0: DataSnapshot) {
                mPlaces = ArrayList()
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

                    (mPlaces as ArrayList).add(Place(title, description, spotList, id))


                }
                updateMap()
                placeAdapter = context?.let {
                    PlaceAdapter(it, mPlaces!!, object : PlaceAdapter.OnClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(context, PlaceCreationActivity::class.java)
                            intent.putExtra("newPlace", false)
                            intent.putExtra("place", mPlaces!![position])

                            startActivity(intent)
                        }

                    })
                }

                recyclerView!!.adapter = placeAdapter

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    private fun updateMap() {
        gMap?.clear()
        val latLngBounds = LatLngBounds.Builder()
        var check = 0
        for (place in mPlaces!!) {
            for (spot in place.spotList) {
                val marker = LatLng(spot.latitude.toDouble(), spot.longitude.toDouble())
                latLngBounds.include(marker)
                check++
                gMap?.addMarker(
                    MarkerOptions().position(marker).title(spot.title)
                        .snippet(spot.description)
                )
            }

        }
        if (check > 0) {

            gMap?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    latLngBounds.build(),
                    1000,
                    1000,
                    0
                )
            )


        }


    }


}