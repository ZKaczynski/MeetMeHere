package pl.bishock.wpam.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pl.bishock.wpam.AdapterClasses.UserAdapter
import pl.bishock.wpam.ModelClasses.Users
import pl.bishock.wpam.R
import java.lang.Error


class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    private var matchedList: List<String> ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.search_text)

        mUsers = ArrayList()

        retrieveMatched()

        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())

            }

            override fun afterTextChanged(s: Editable?) {

            }


        })

        return view
    }

    private fun retrieveMatched() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val refMatched = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUserId).child("Matched")

        refMatched.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                matchedList = ArrayList<String>()
                for (snapshot in p0.children) {
                    val user = snapshot.value.toString()
                        (matchedList as ArrayList<String>).add(user)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveAllUsers() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")


        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                mUsers = ArrayList<Users>()
                (mUsers as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString() == "") {
                    for (snapshot in p0.children) {
                        val user = snapshot.getValue(Users::class.java)

                        if (user!!.uid != firebaseUserId && matchedList?.contains(user.uid)!!) {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }
                    try {
                        userAdapter = UserAdapter(context!!, mUsers!!, false)
                    } catch (e: Error) {

                    }

                    recyclerView!!.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

    private fun searchForUsers(username: String) {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid


        val queryUsers =
            FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
                .startAt(username).endAt(username + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user = snapshot.getValue(Users::class.java)
                    if (user!!.uid != firebaseUserId && matchedList?.contains(user.uid)!!) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }
}