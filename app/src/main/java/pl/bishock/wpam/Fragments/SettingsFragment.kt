package pl.bishock.wpam.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import pl.bishock.wpam.AboutEditActivity
import pl.bishock.wpam.MainActivity
import pl.bishock.wpam.ModelClasses.Users
import pl.bishock.wpam.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SettingsFragment : Fragment() {

    var userReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    private var RequestCode = 438

    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null

    private var coverChecker: String? = ""
    private var linkChecker: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        val genderSpinner: Spinner = view.findViewById(R.id.gender_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.sex,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
            genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val array = resources.getStringArray(R.array.sex)
                    val hashMap = HashMap<String, Any>()
                    hashMap["gender"] = array[position]
                    userReference!!.updateChildren(hashMap)
                }
            }
        }

        val lookingSpinner: Spinner = view.findViewById(R.id.looking_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.sex,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            lookingSpinner.adapter = adapter
            lookingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val array = resources.getStringArray(R.array.sex)
                    val hashMap = HashMap<String, Any>()
                    hashMap["looking"] = array[position]
                    userReference!!.updateChildren(hashMap)
                }
            }
        }

        val ageSpinner: Spinner = view.findViewById(R.id.age_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.age,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ageSpinner.adapter = adapter
            ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val array = resources.getStringArray(R.array.age)
                    val hashMap = HashMap<String, Any>()
                    hashMap["age"] = array[position]
                    userReference!!.updateChildren(hashMap)
                }
            }
        }
        val maritalSpinner: Spinner = view.findViewById(R.id.marital_status_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.martial_status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            maritalSpinner.adapter = adapter
            maritalSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val array = resources.getStringArray(R.array.martial_status)
                        val hashMap = HashMap<String, Any>()
                        hashMap["marital"] = array[position]
                        userReference!!.updateChildren(hashMap)
                    }
                }

        }

        val heightSpinner: Spinner = view.findViewById(R.id.height_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.height,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            heightSpinner.adapter = adapter
            heightSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val array = resources.getStringArray(R.array.height)
                        val hashMap = HashMap<String, Any>()
                        hashMap["height"] = array[position]
                        userReference!!.updateChildren(hashMap)
                    }
                }

        }

        val figureSpinner: Spinner = view.findViewById(R.id.figure_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.figure,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            figureSpinner.adapter = adapter
            figureSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val array = resources.getStringArray(R.array.figure)
                        val hashMap = HashMap<String, Any>()
                        hashMap["figure"] = array[position]
                        userReference!!.updateChildren(hashMap)
                    }
                }

        }


        val kidsSpinner: Spinner = view.findViewById(R.id.kids_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.kids,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            kidsSpinner.adapter = adapter
            kidsSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val array = resources.getStringArray(R.array.kids)
                        val hashMap = HashMap<String, Any>()
                        hashMap["kids"] = array[position]
                        userReference!!.updateChildren(hashMap)
                    }
                }

        }

        val cigarettesSpinner: Spinner = view.findViewById(R.id.smoke_spinner)
        ArrayAdapter.createFromResource(
            context as Context,
            R.array.cigarettes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cigarettesSpinner.adapter = adapter
            cigarettesSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val array = resources.getStringArray(R.array.cigarettes)
                        val hashMap = HashMap<String, Any>()
                        hashMap["cigarettes"] = array[position]
                        userReference!!.updateChildren(hashMap)
                    }
                }

        }

        view.about_text_view.setOnClickListener{
            val intent = Intent(context, AboutEditActivity::class.java)
            startActivity(intent)

        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userReference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (context != null) {
                        val user: Users? = snapshot.getValue(Users::class.java)

                        view.username_settings?.text = user?.username

                        val genderIndex =
                            resources.getStringArray(R.array.sex).indexOf(user?.gender)
                        if (genderIndex == 0) {
                            view.gender_image.setImageResource(R.drawable.male)
                            genderSpinner.setSelection(genderIndex)
                        } else if (genderIndex == 1) {
                            view.gender_image.setImageResource(R.drawable.female)
                            genderSpinner.setSelection(genderIndex)
                        }

                        val lookingIndex =
                            resources.getStringArray(R.array.sex).indexOf(user?.looking)
                        if (lookingIndex == 0) {
                            view.looking_image.setImageResource(R.drawable.male)
                            lookingSpinner.setSelection(lookingIndex)
                        } else if (lookingIndex == 1) {
                            view.looking_image.setImageResource(R.drawable.female)
                            lookingSpinner.setSelection(lookingIndex)
                        }

                        val ageIndex = resources.getStringArray(R.array.age).indexOf(user?.age)
                        if (ageIndex != -1) {
                            ageSpinner.setSelection(ageIndex)
                        }

                        val maritalIndex =
                            resources.getStringArray(R.array.martial_status).indexOf(user?.marital)
                        if (maritalIndex != -1) {
                            maritalSpinner.setSelection(maritalIndex)
                        }

                        val heightIndex =
                            resources.getStringArray(R.array.height).indexOf(user?.height)
                        if (heightIndex != -1) {
                            heightSpinner.setSelection(heightIndex)
                        }

                        val figureIndex =
                            resources.getStringArray(R.array.figure).indexOf(user?.figure)
                        if (figureIndex != -1) {
                            figureSpinner.setSelection(figureIndex)
                        }

                        val kidsIndex =
                            resources.getStringArray(R.array.kids).indexOf(user?.kids)
                        if (kidsIndex != -1) {
                            kidsSpinner.setSelection(kidsIndex)
                        }

                        val cigarettesIndex =
                            resources.getStringArray(R.array.cigarettes).indexOf(user?.cigarettes)
                        if (cigarettesIndex != -1) {
                            cigarettesSpinner.setSelection(cigarettesIndex)
                        }

                        view.about_text_view.text = user?.about

                        Picasso.get().load(user?.profile).placeholder(R.drawable.ic_profile)
                            .into(view.profile_settings)
                        Picasso.get().load(user?.background).placeholder(R.drawable.background)
                            .into(view.background_image)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }

        })

        view.profile_settings.setOnClickListener {
            pickImage()
        }

        view.background_image.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        return view
    }

    private fun changeLink() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (linkChecker == "website") {
            builder.setTitle("Write URL")
        } else {
            builder.setTitle("Write username")
        }

        val editText = EditText(context)

        editText.hint = "input here"

        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val text = editText.text.toString()
            if (text == "") {
                Toast.makeText(context, "Empty!", Toast.LENGTH_LONG).show()
            } else {
                setLink(text)
            }

        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()

        })
        builder.show()
    }

    private fun setLink(text: String) {
        if (linkChecker == "website") {
            val mapCover = HashMap<String, Any>()
            mapCover["website"] = text
            userReference!!.updateChildren(mapCover)
            linkChecker = ""
        } else if (linkChecker == "facebook") {
            val mapCover = HashMap<String, Any>()
            mapCover["facebook"] = text
            userReference!!.updateChildren(mapCover)
            linkChecker = ""
        } else {
            val mapCover = HashMap<String, Any>()
            mapCover["instagram"] = text
            userReference!!.updateChildren(mapCover)
            linkChecker = ""

        }

    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Uploading...", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {


        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {

                        val mapCover = HashMap<String, Any>()
                        mapCover["background"] = url
                        userReference!!.updateChildren(mapCover)
                        coverChecker = ""

                    } else {
                        val mapCover = HashMap<String, Any>()
                        mapCover["profile"] = url
                        userReference!!.updateChildren(mapCover)
                        coverChecker = ""
                    }

                }


            }
        }
    }

}