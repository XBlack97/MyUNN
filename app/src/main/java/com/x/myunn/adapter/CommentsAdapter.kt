package com.x.myunn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.x.myunn.R
import com.x.myunn.activities.MainActivity
import com.x.myunn.model.Comment
import com.x.myunn.model.User
import com.x.myunn.ui.postDetail.PostDetailFragmentDirections


class CommentsAdapter(private val c: Context, private val commentList: MutableList<Comment>?) :
    RecyclerView.Adapter<CommentsAdapter.Viewholder>() {

    private var firebaseUser: FirebaseUser? = null

    private val main = MainActivity.newInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(c).inflate(R.layout.item_layout_comment, parent, false)


        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return commentList!!.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = commentList!![position]

        holder.comment.text = comment.comment
        holder.commentTime.text = comment.time

        getUserInfo(holder.imageProfile, holder.userName, comment.publisher)

        holder.imageProfile.setOnClickListener {
            val action = PostDetailFragmentDirections
                .actionPostDetailFragmentToNavProfile(comment.publisher)
            it.findNavController().navigate(action)
        }

    }

    private fun getUserInfo(imageProfile: ImageView, userName: TextView, publisher: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue(User::class.java)

                    userName.text = user!!.username

                    main.glideLoad(c, user.image, imageProfile)


                }
            }
        })

    }


    inner class Viewholder(v: View) : RecyclerView.ViewHolder(v) {

        var imageProfile: ImageView = v.findViewById(R.id.user_profile_image_comment)
        var userName: TextView = v.findViewById(R.id.user_name_comment)
        var comment: TextView = v.findViewById(R.id.comment_text_comment)
        var commentTime: TextView = v.findViewById(R.id.comment_time)

    }
}
