package com.x.myunn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.x.myunn.R
import com.x.myunn.model.Comment
import com.x.myunn.model.User

import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter(private val c: Context, private val commentList: MutableList<Comment>?) :
    RecyclerView.Adapter<CommentsAdapter.Viewholder>() {

    private var firebaseUser: FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(c).inflate(R.layout.comments_item_layout, parent, false)


        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return commentList!!.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = commentList!![position]

        holder.commet.text = comment.comment

        getUserInfo(holder.imageProfile, holder.userName, comment.publisher)

    }

    private fun getUserInfo(imageProfile: CircleImageView, userName: TextView, publisher: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue(User::class.java)

                    userName.text = user!!.username

                    Glide.with(c)
                        .load(user.image)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .into(imageProfile)


                }
            }
        })

    }


    inner class Viewholder(v: View) : RecyclerView.ViewHolder(v) {

        var imageProfile: CircleImageView
        var userName: TextView
        var commet: TextView

        init {
            imageProfile = v.findViewById(R.id.user_profile_image_comment)
            userName = v.findViewById(R.id.user_name_comment)
            commet = v.findViewById(R.id.comment_text_comment)

        }

    }
}
