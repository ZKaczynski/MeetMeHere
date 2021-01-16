package pl.bishock.wpam.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import pl.bishock.wpam.MessageChatActivity
import pl.bishock.wpam.ModelClasses.Chat
import pl.bishock.wpam.ModelClasses.Users
import pl.bishock.wpam.PlaceViewActivity
import pl.bishock.wpam.R
import pl.bishock.wpam.ViewProfileActivity

class UserAdapter(
        mContext: Context,
        mUsers: List<Users>,
        isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>(){


    private var lastMessage :String =""
    private val mContext: Context
    private val mUsers: List<Users>
    private val isChatCheck: Boolean
    init {
        this.mContext = mContext
        this.mUsers = mUsers
        this.isChatCheck = isChatCheck
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var usernameText : TextView
        var profileImageView: CircleImageView
        var onlineImageView: ImageView
        var offlineImageView: ImageView
        var lastMessageText: TextView


        init {
            usernameText = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageText = itemView.findViewById(R.id.message_last)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_user_search_layout,parent,false)
        return  UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.usernameText.text = user.username
        Picasso.get().load(user.profile)
            .placeholder(R.drawable.ic_profile)
            .into(holder.profileImageView)

        if (isChatCheck){
            retrieveLastMessage(user.uid, holder.lastMessageText)
        }else{
            holder.lastMessageText.visibility = View.GONE
        }

        if (isChatCheck){
            if (user.status == "online"){
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            }else{
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        }else{
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            val options = arrayOf<CharSequence>("Send Message", "View Profile", "View Places")
            val builder : AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("Choose action")
            builder.setItems(options, DialogInterface.OnClickListener{
              dialog, which ->
                if (which == 1){
                    val intent = Intent(mContext, ViewProfileActivity::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)
                }else if (which == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)

                }else if (which == 2){
                    val intent = Intent(mContext, PlaceViewActivity::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)
                }
            })
            builder.show()

        }
    }

    private fun retrieveLastMessage(otherId: String, lastMessageText: TextView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference  = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(s0: DataSnapshot) {
                for (snapshot in s0.children){
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        if (chat.sender == firebaseUser!!.uid && chat.receiver == otherId ||
                            chat.sender == otherId && chat.receiver == firebaseUser.uid){
                            lastMessage = chat.message
                        }
                    }
                }
                if (lastMessage != ""){
                    lastMessageText.text = lastMessage
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

}
