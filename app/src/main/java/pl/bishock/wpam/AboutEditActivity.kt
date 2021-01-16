package pl.bishock.wpam

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_about_edit.*
import pl.bishock.wpam.ModelClasses.Users

class AboutEditActivity : AppCompatActivity() {

    private var userReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_edit)
        setSupportActionBar(about_edit_toolbar)
        supportActionBar!!.title = "About me"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        about_edit_toolbar.setNavigationOnClickListener {
            val intent = Intent(this@AboutEditActivity, MainActivity::class.java)
            startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)


        userReference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    about_edit.setText(user?.about, TextView.BufferType.EDITABLE)

                }
            }

            override fun onCancelled(error: DatabaseError) {


            }

        })

        about_button.setOnClickListener {
            val hashMap = HashMap<String, Any>()
            hashMap["about"] = about_edit.text.toString()
            userReference!!.updateChildren(hashMap)
            finish()
        }

    }
}