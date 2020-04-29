package com.x.myunn.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.x.myunn.R
import com.x.myunn.activities.PostDetailActivty
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Post
import com.x.myunn.model.User
import com.x.myunn.ui.profile.ProfileFragment
import com.x.myunn.ui.showUsers.ShowUsersFragment
import de.hdodenhof.circleimageview.CircleImageView

private const val TAG = "PostAdapter"

class PostAdapter(private val c: Context, private val mPost: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    lateinit var numOfLikes: String
    lateinit var numOfComments: String
    lateinit var postUsername: String
    lateinit var postUserImage: String
    lateinit var postDescription: String
    lateinit var postTime: String


    val firebaseRepo = FirebaseRepo()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.post_layout, parent, false)


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost[position]

        holder.postDescription.text = post.postDescription
        postDescription = holder.postDescription.text.toString()

        holder.postTime.text = post.PostTime
        postTime = holder.postTime.text.toString()


        publisherInfo(holder.profileImage, holder.userName, post.publisher)
        firebaseRepo.isLike(post.postId, holder.likeBtn)
        numberOfLikes(holder.likes, post.postId)
        numberOfComments(holder.comments, post.postId)
        firebaseRepo.checkSavedStatus(post.postId, holder.saveBtn)

        holder.postDescription.setOnClickListener {

            moveToPostDetail(post.postId, post.publisher)
        }

        holder.itemView.setOnClickListener {
            moveToPostDetail(post.postId, post.publisher)
        }


        holder.profileImage.setOnClickListener {
            val editor = c.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("profileId", post.publisher)
            editor.apply()

            (c as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ProfileFragment())
                .addToBackStack(null)
                .commit()

        }

        holder.profileImage.setOnClickListener {
            val editor = c.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("profileId", post.publisher)
            editor.apply()

            (c as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ProfileFragment())
                .addToBackStack(null)
                .commit()

        }


        holder.likeBtn.setOnClickListener {
            if (holder.likeBtn.tag == "Like") {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postId)
                    .child(firebaseUser!!.uid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postId)
                    .child(firebaseUser!!.uid).removeValue()

            }

            firebaseRepo.addNotificationLike(post.publisher, post.postId)
        }

        holder.commentBtn.setOnClickListener {

            moveToPostDetail(post.postId, post.publisher)
        }

        holder.comments.setOnClickListener {

            moveToPostDetail(post.postId, post.publisher)
        }

        holder.saveBtn.setOnClickListener {
            if (holder.saveBtn.tag == "Unsaved") {

                FirebaseDatabase.getInstance().reference.child("Saves")
                    .child(firebaseUser!!.uid).child(post.postId).setValue(true)

            } else {
                FirebaseDatabase.getInstance().reference.child("Saves")
                    .child(firebaseUser!!.uid).child(post.postId).removeValue()


            }
        }

        holder.likes.setOnClickListener {

            val editor =
                (c as FragmentActivity).getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("id", post.postId)
            editor.putString("title", "Likes")
            editor.apply()

            c.supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ShowUsersFragment())
                .addToBackStack(null)
                .commit()
        }

    }


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var profileImage: CircleImageView = v.findViewById(R.id.post_user_profile_image)
        var likeBtn: ImageView = v.findViewById(R.id.post_like_btn)
        var postTime: TextView = v.findViewById(R.id.post_time)
        var commentBtn: ImageView = v.findViewById(R.id.post_comment_btn)
        var saveBtn: ImageView = v.findViewById(R.id.post_save_btn)
        var userName: TextView = v.findViewById(R.id.post_user_name)
        var likes: TextView = v.findViewById(R.id.post_like_text)

        //var description: TextView = v.findViewById(R.id.comments_description)
        var comments: TextView = v.findViewById(R.id.post_comment_text)
        var postDescription: TextView = v.findViewById(R.id.post_description)

    }

    private fun moveToPostDetail(postId: String, publisher: String) {

        val detailIntent = Intent(c, PostDetailActivty::class.java)

        detailIntent.putExtra("postId", postId)
        detailIntent.putExtra("publisherId", publisher)

        c.startActivity(detailIntent)
    }


    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisherId: String
    ) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(ds: DataSnapshot) {
                if (ds.exists()) {
                    val user = ds.getValue(User::class.java)!!


                    Glide.with(c)
                        .load(user.image)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .into(profileImage)

                    userName.text = user.username

                    postUserImage = user.image
                    postUsername = user.username
                }
            }

        })

    }

    private fun numberOfLikes(likes: TextView, postId: String) {

        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    numOfLikes = p0.childrenCount.toString() + " likes"
                    likes.text = numOfLikes
                }
            }
        })
    }

    private fun numberOfComments(comments: TextView, postId: String) {

        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    numOfComments = p0.childrenCount.toString() + " comments"
                    comments.text = numOfComments
                }
            }
        })
    }


}






