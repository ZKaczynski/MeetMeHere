package pl.bishock.wpam.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pl.bishock.wpam.AdapterClasses.UserAdapter
import pl.bishock.wpam.ModelClasses.ChatList
import pl.bishock.wpam.ModelClasses.Users
import pl.bishock.wpam.R

class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var userChatList: List<ChatList>? = null

    lateinit var recycler_view_chatlist: RecyclerView

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chat_list)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userChatList = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s0: DataSnapshot) {

                (userChatList as ArrayList).clear()

                for (snapshot in s0.children){
                    val chatlist = snapshot.getValue(ChatList::class.java)
                    (userChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        return view
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (snapshot in p0.children) {
                    val user = snapshot.getValue(Users::class.java)

                    for (eachChatList in userChatList!!) {
                        if (user!!.uid == eachChatList.id) {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList), true)
                recycler_view_chatlist.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }


}