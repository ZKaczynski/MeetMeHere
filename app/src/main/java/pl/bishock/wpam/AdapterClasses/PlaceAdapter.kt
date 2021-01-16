package pl.bishock.wpam.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import pl.bishock.wpam.MessageChatActivity
import pl.bishock.wpam.ModelClasses.Place
import pl.bishock.wpam.PlaceViewActivity
import pl.bishock.wpam.R
import pl.bishock.wpam.ViewProfileActivity

class PlaceAdapter(
    private val mContext: Context,
    private val mPlaces: List<Place>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<PlaceAdapter.ViewHolder?>() {

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleText: TextView = itemView.findViewById(R.id.title)
        var descriptionText: TextView = itemView.findViewById(R.id.description)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.item_place_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = mPlaces[position]
        holder.titleText.text = place.title
        holder.descriptionText.text = place.description

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            val options = arrayOf<CharSequence>("Delete")
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("Choose action")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(firebaseUser!!)
                        .child("Places")
                        .child(place.id)
                        .removeValue()
                }
            })
            builder.show()
            true
        }

    }


    override fun getItemCount(): Int {
        return mPlaces.size
    }
}
