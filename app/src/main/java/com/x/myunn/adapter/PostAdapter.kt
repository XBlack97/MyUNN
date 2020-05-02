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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.x.myunn.R
import com.x.myunn.activities.MainActivity
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Post
import com.x.myunn.model.User
import com.x.myunn.ui.home.HomeFragmentDirections
import com.x.myunn.ui.profile.ProfileFragmentDirections
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val c: Context?,
                  private var mPost: MutableList<Post>?,
                    private var isProfile: Boolean = false) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    companion object {
        fun newInstance() = PostAdapter(null, null)
    }


    private val firebaseUser = FirebaseAuth.getInstance().currentUser


    val firebaseRepo = FirebaseRepo()

    val main = MainActivity.newInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.post_layout, parent, false)


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return mPost!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost!![position]

        holder.postDescription.text = post.postDescription

        holder.postTime.text = post.PostTime


        publisherInfo(holder.profileImage, holder.userName, post.publisher, c!!)
        firebaseRepo.isLike(post.postId, holder.likeBtn)
        numberOfLikes(holder.likes, post.postId)
        numberOfComments(holder.comments, post.postId)
        firebaseRepo.checkSavedStatus(post.postId, holder.saveBtn)

        holder.postDescription.setOnClickListener {
            if(!isProfile){
                homeToPostDetail(it, post.postId, post.publisher)
            }
        }

        holder.itemView.setOnClickListener {
            if(isProfile){
                profileToPostDetail(it, post.postId, post.publisher )
            }else
                homeToPostDetail(it, post.postId, post.publisher)
        }


        holder.profileImage.setOnClickListener {

            if(!isProfile){
                moveToProfile(it, post.publisher)
            }
        }


        holder.likeBtn.setOnClickListener {

            like(holder.likeBtn, post.postId, post.publisher)
        }

        holder.commentBtn.setOnClickListener {
            if(isProfile){
                profileToPostDetail(it, post.postId, post.publisher )
            }else
            homeToPostDetail(it, post.postId, post.publisher)
        }

        holder.comments.setOnClickListener {
            if(isProfile){
                profileToPostDetail(it, post.postId, post.publisher )
            }else
            homeToPostDetail(it, post.postId, post.publisher)
        }

        holder.saveBtn.setOnClickListener {

            save(holder.saveBtn, post.postId)

        }

        holder.likes.setOnClickListener {

            if(isProfile){
                val action = ProfileFragmentDirections
                    .actionNavProfileToNavShowusers("Likes", post.postId)
                it.findNavController().navigate(action)

            }else {
                val action = HomeFragmentDirections
                    .actionNavHomeToNavShowusers("Likes", post.postId)
                it.findNavController().navigate(action)
            }

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

    fun save(saveBtn: ImageView, postId: String) {

        if (saveBtn.tag == "Unsaved") {

            FirebaseDatabase.getInstance().reference.child("Saves")
                .child(firebaseUser!!.uid).child(postId).setValue(true)

        } else {
            FirebaseDatabase.getInstance().reference.child("Saves")
                .child(firebaseUser!!.uid).child(postId).removeValue()


        }
    }

    fun like(likeBtn: ImageView, postId: String, publisher: String) {

        if (likeBtn.tag == "Like") {
            FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
                .child(firebaseUser!!.uid).setValue(true)
        } else {
            FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
                .child(firebaseUser!!.uid).removeValue()

        }

        firebaseRepo.addNotificationLike(publisher, postId)

    }

    private fun homeToPostDetail(v: View, postId: String, publisher: String) {

        val action = HomeFragmentDirections
            .actionNavHomeToPostDetailFragment(postId, publisher)
        v.findNavController().navigate(action)

    }

    private fun profileToPostDetail(v: View, postId: String, publisher: String) {

        val action = ProfileFragmentDirections
            .actionNavProfileToPostDetailFragment(postId , publisher)
        v.findNavController().navigate(action)

    }

    private fun moveToProfile(v: View, publisher: String) {

        val action = HomeFragmentDirections
            .actionNavHomeToNavProfile(publisher)
        v.findNavController().navigate(action)

    }


     fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisherId: String, c: Context
    ) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(ds: DataSnapshot) {
                if (ds.exists()) {
                    val user = ds.getValue(User::class.java)!!


                    main.glideLoad(c, user.image, profileImage)

                    userName.text = user.username
                }
            }

        })

    }

    fun numberOfLikes(likes: TextView, postId: String) {

        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    likes.text = p0.childrenCount.toString() + " likes"
                }
            }
        })
    }

    fun numberOfComments(comments: TextView, postId: String) {

        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    comments.text = p0.childrenCount.toString() + " comments"
                }
            }
        })
    }


}






