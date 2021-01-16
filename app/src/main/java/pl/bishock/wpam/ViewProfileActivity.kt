package pl.bishock.wpam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_message_chat.chat_profile_image
import kotlinx.android.synthetic.main.activity_view_profile.*
import kotlinx.android.synthetic.main.activity_view_profile.about_text_view
import kotlinx.android.synthetic.main.activity_view_profile.background_image
import kotlinx.android.synthetic.main.activity_view_profile.gender_image
import kotlinx.android.synthetic.main.activity_view_profile.looking_image
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import pl.bishock.wpam.AdapterClasses.ChatAdapter
import pl.bishock.wpam.ModelClasses.Chat
import pl.bishock.wpam.ModelClasses.Users

class ViewProfileActivity : AppCompatActivity() {
    var visitId: String = ""
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        setSupportActionBar(view_profile_toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        view_profile_toolbar.setNavigationOnClickListener {
            val intent = Intent(this@ViewProfileActivity, MainActivity::class.java)
            startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        }
        visitId = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(visitId)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot != null) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    view_profile_username.text = user!!.username
                    Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(view_profile_profile_image)
                    Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(profile_image)
                    Picasso.get().load(user.background).into(background_image)
                    username_text.text = user.username
                    gender_text_view.text = user.gender
                    looking_text_view.text = user.looking
                    age_text_view.text = user.age
                    height_text_view.text = user.height
                    marital_status_text_view.text = user.marital
                    figure_text_view.text = user.figure
                    kids_text_view.text = user.kids
                    smoke_text_view.text = user.cigarettes
                    about_text_view.text = user.about

                    if (user.gender == "Male"){
                        gender_image.setImageResource(R.drawable.male)
                    }else if (user.gender == "Female"){
                        gender_image.setImageResource(R.drawable.female)
                    }
                    if (user.looking == "Male"){
                        looking_image.setImageResource(R.drawable.male)
                    }else if (user.looking == "Female"){
                        looking_image.setImageResource(R.drawable.female)
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}