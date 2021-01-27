package pl.bishock.wpam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_register)

        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        val button: Button = findViewById(R.id.button_register)
        button.setOnClickListener(){
            registerUser()
        }
    }

    private fun registerUser() {

        val email: String =  email_register.text.toString()
        val username: String =  username_register.text.toString()
        val password1:String = password_register1.text.toString()
        val password2:String = password_register2.text.toString()

        if (email==""){
            Toast.makeText(baseContext, "Enter email", Toast.LENGTH_SHORT).show()
        }else if (password1 != password2){
            Toast.makeText(baseContext, "Passwords not match", Toast.LENGTH_SHORT).show()
        }
        else{
        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        firebaseUserId = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId)
                        val userHashMap = HashMap<String,Any>()
                        userHashMap["uid"]=firebaseUserId
                        userHashMap["username"]=username
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/wpam-2020.appspot.com/o/profile.jpg?alt=media&token=5023c109-0ba7-433a-84b2-31f24e6b99e1"
                        userHashMap["background"] = "https://firebasestorage.googleapis.com/v0/b/wpam-2020.appspot.com/o/background.jpg?alt=media&token=7be877d1-9ddd-4520-a4af-7685e2fe1762"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["status"] = "offline"
                        userHashMap["gender"] = ""
                        userHashMap["looking"] = ""
                        userHashMap["age"] = ""
                        userHashMap["marital"] = ""
                        userHashMap["height"] = ""
                        userHashMap["figure"] = ""
                        userHashMap["kids"] = ""
                        userHashMap["cigarettes"] = ""
                        userHashMap["about"] = ""
                        userHashMap["Matched"] = ""

                        refUsers.updateChildren(userHashMap).addOnCompleteListener{task->
                            if (task.isSuccessful){
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                finish()
                            }


                        }
                    } else {

                        Toast.makeText(baseContext, "Registration failed."+task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()

                    }

                    // ...
                }

        }
    }
}