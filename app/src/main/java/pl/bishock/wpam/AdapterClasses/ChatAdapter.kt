package pl.bishock.wpam.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import pl.bishock.wpam.ModelClasses.Chat
import pl.bishock.wpam.R

class ChatAdapter(
    private val mContext: Context,
    private val mChatList: List<Chat>,
    private val imageUrl: String
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1) {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_message_right, parent, false)
            ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_message_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        return if (mChatList[position].sender==firebaseUser!!.uid){
            1
        }else{
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_message_left)

        if (chat.message == "Sent you an  image." && chat.url!="") {

            if (chat.sender == firebaseUser!!.uid) {
                holder.text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.right_image_view)
            } else {
                holder.text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.left_image_view)
            }
        }else{
            holder.text_message!!.text = chat.message
        }

        if (position == mChatList.size-1){
            if (chat.isSeen) {

                holder.text_seen!!.text = "Seen"

                if (chat.message == "Sent you an  image." && chat.url !== "") {

                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }else{
                holder.text_seen!!.text = "Sent"

                if (chat.message == "Sent you an  image." && chat.url !== "") {

                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }else{
            holder.text_seen!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_message_left: CircleImageView? = null
        var text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null


        init {
            profile_message_left = itemView.findViewById(R.id.profile_message_left)
            text_message = itemView.findViewById(R.id.text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }
    }
}
