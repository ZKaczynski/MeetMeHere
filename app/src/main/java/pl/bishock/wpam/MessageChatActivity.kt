package pl.bishock.wpam

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import pl.bishock.wpam.AdapterClasses.ChatAdapter
import pl.bishock.wpam.ModelClasses.Chat
import pl.bishock.wpam.ModelClasses.Users

class MessageChatActivity : AppCompatActivity() {
    private var RequestCode = 438
    var visitId: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatAdapter: ChatAdapter? = null
    var mChatList: List<Chat>? = null
    var reference: DatabaseReference? = null

    lateinit var rvChats: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        setSupportActionBar(chat_toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        chat_toolbar.setNavigationOnClickListener {
            val intent = Intent(this@MessageChatActivity, MainActivity::class.java)
            startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        }

        visitId = intent.getStringExtra("visit_id").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        rvChats = chat_recycler
        rvChats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        rvChats.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference
                .child("Users").child(visitId)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                chat_username.text = user!!.username
                Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(chat_profile_image)

                retrieveMessages(firebaseUser!!.uid, visitId, user.profile)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        chat_send.setOnClickListener {
            val message = chat_message.text.toString()
            if (message == "") {
                Toast.makeText(baseContext, "Empty message", Toast.LENGTH_SHORT).show()
            } else {

                sendMessageToUser(firebaseUser!!.uid, visitId, message)
            }
            chat_message.setText("")
        }

        chat_upload.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select image"), RequestCode)
        }
        seenMessage(visitId)

    }

    private fun retrieveMessages(senderId: String, visitId: String, imageUrl: String) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList).clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.sender == senderId && chat.receiver == visitId
                            || chat.sender == visitId && chat.receiver == senderId) {
                        (mChatList as ArrayList).add(chat)
                    }
                    chatAdapter = ChatAdapter(this@MessageChatActivity, mChatList as ArrayList<Chat>, imageUrl)
                    rvChats.adapter = chatAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessageToUser(uid: String, visitId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key.toString()
        val messageHashMap = HashMap<String, Any>()
        messageHashMap["sender"] = uid
        messageHashMap["message"] = message
        messageHashMap["receiver"] = visitId
        messageHashMap["isSeen"] = false
        messageHashMap["messageId"] = messageKey
        messageHashMap["url"] = ""

        reference.child("Chats").child(messageKey).setValue(messageHashMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val chatListReference = FirebaseDatabase.getInstance()
                                .reference.child("ChatList").child(firebaseUser!!.uid).child(visitId)

                        chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                    chatListReference.child("id").setValue(visitId)
                                }

                                val chatListReceiverReference = FirebaseDatabase.getInstance()
                                        .reference.child("ChatList").child(visitId).child(firebaseUser!!.uid)
                                chatListReceiverReference.child("id").setValue(firebaseUser!!.uid)
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }


                        })



                       // chatListReference.child("id").setValue(firebaseUser!!.uid)

                        val reference = FirebaseDatabase.getInstance().reference
                                .child("Users").child(firebaseUser!!.uid)

                        //implemetn notifiaction
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == RESULT_OK && data != null && data!!.data != null) {


            val fileUri = data.data
            var storagReference = FirebaseStorage.getInstance().reference.child("Chat images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key.toString()
            val filePath = storagReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                var downloadUrl = task.result
                val url = downloadUrl.toString()
                val messageHashMap = HashMap<String, Any>()
                messageHashMap["sender"] = firebaseUser!!.uid
                messageHashMap["message"] = "Sent you an  image."
                messageHashMap["receiver"] = visitId
                messageHashMap["isSeen"] = false
                messageHashMap["messageId"] = messageId
                messageHashMap["url"] = url
                ref.child("Chats").child(messageId!!).setValue(messageHashMap)


            }
        }
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s0: DataSnapshot) {
                for (snapshot in s0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == firebaseUser!!.uid && chat.sender == userId) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isSeen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}