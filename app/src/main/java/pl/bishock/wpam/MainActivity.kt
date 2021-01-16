package pl.bishock.wpam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import pl.bishock.wpam.Fragments.*
import pl.bishock.wpam.ModelClasses.Chat
import pl.bishock.wpam.ModelClasses.Users

class MainActivity : AppCompatActivity() {

    private var userReference: DatabaseReference? = null
    var firebaseUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""

        firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
        userReference = firebaseUser?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(
                it
            )
        }

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    username.text = user?.username
                    Picasso.get().load(user?.profile).placeholder(R.drawable.ic_profile)
                        .into(profile_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s0: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessages = 0

                for (snapshot in s0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (!chat!!.isSeen && chat.receiver == firebaseUser) {
                        countUnreadMessages++
                    }
                }
                if (countUnreadMessages == 0) {
                    viewPagerAdapter.addFragment(ChatFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatFragment(), "($countUnreadMessages)Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(), "Paired")
                viewPagerAdapter.addFragment(SettingsFragment(), "Profile")
                viewPagerAdapter.addFragment(MyPlacesFragment(), "My Spots")
                //viewPagerAdapter.addFragment(UserMap(), "My Map")


                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                true
            }
            else -> false
        }
    }


    private fun updateStatus(status: String) {
        val ref = firebaseUser?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(
                it
            )
        }
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragments: ArrayList<Fragment> = ArrayList()
        private val titles: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}