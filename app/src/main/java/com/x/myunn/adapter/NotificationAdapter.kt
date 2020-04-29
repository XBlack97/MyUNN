package com.x.myunn.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.activities.PostDetailActivty
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Notification
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(
    private var c: Context,
    private var notifications: MutableList<Notification>
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    val firebaseRepo = FirebaseRepo()
    var navController =
        Navigation.findNavController((c as FragmentActivity), R.id.nav_host_fragment)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.notification_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification = notifications[position]

        if (notification.text == "started following you") {
            holder.n_text.text = "started following you"
        } else if (notification.text == "liked your post") {
            holder.n_text.text = "liked your post"
        } else if (notification.text.contains("commented:")) {
            holder.n_text.text = notification.text.replace("commented:", "commented: ")
        } else {
            holder.n_text.text = notification.text
        }


        firebaseRepo.loadUserInfo(
            notification.userId,
            holder.profileImage,
            null,
            null,
            holder.userName,
            null,
            c
        )

        if (notification.Post) {
            holder.postImage.visibility = View.VISIBLE
            //getPostImage(holder.postImage, notification.postId)
        } else {
            holder.postImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (notification.Post) {
                val detailIntent = Intent(c, PostDetailActivty::class.java)
                detailIntent.putExtra("postId", notification.postId)
                detailIntent.putExtra("publisherId", notification.userId)

                c.startActivity(detailIntent)

            } else {

                val editor = c.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileId", notification.userId)
                editor.apply()

                navController.navigate(R.id.action_navigation_notifications_to_nav_profile)
            }
        }

    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var postImage = v.findViewById<ImageView>(R.id.post_image_notification)
        var profileImage = v.findViewById<CircleImageView>(R.id.profile_image_notification)
        var userName = v.findViewById<TextView>(R.id.username_notification)
        var n_text = v.findViewById<TextView>(R.id.comment_notification)

    }


    /**
    private fun getPostImage(imageView: ImageView, postID: String) {
    val postRef =
    FirebaseDatabase.getInstance().reference.child("Posts").child(postID)

    postRef.addValueEventListener(object : ValueEventListener {
    override fun onDataChange(ds: DataSnapshot) {

    if (ds.exists()) {
    val post = ds.getValue(Post::class.java)!!

    println("Notification Image: " + post.postImage.toString())

    Picasso.get().load(post.postImage).placeholder(R.drawable.profile)
    .into(imageView)
    }

    }

    override fun onCancelled(p0: DatabaseError) {

    }
    })
    }
     */
}