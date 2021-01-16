package pl.bishock.wpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_login)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        button_login.setOnClickListener {
            login()
        }


    }

    private fun login() {
        val email: String = email_login.text.toString()
        val password: String = password_login.text.toString()

        if (email == "") {
            Toast.makeText(baseContext, "Enter email", Toast.LENGTH_SHORT).show()
        } else if (password == "") {
            Toast.makeText(baseContext, "Passwords not match", Toast.LENGTH_SHORT).show()
        } else {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            finish()
                        } else {
                            Toast.makeText(baseContext, "Login failed:+", Toast.LENGTH_SHORT).show()

                        }


                    }
        }
    }
}