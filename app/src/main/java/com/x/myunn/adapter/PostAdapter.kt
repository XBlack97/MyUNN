package com.x.myunn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Post
import com.x.myunn.model.PostImage
import com.x.myunn.ui.home.HomeFragmentDirections
import com.x.myunn.ui.profile.ProfileFragmentDirections
import com.x.myunn.ui.starredPost.StarredPostFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostAdapter(
    private val c: Context,
    private var mPost: MutableList<Post>,
    private var postPage: Int //, private var mPostImages: MutableList<PostImage>
) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    val firebaseRepo = FirebaseRepo()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(c).inflate(R.layout.item_layout_post, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost[position]

        holder.postDescription.text = post.postDescription

        holder.postTime.text = post.PostTime

        if (post._isPostImages){
            holder.postImages.visibility = View.GONE
        }

        holder.postImages.visibility = View.VISIBLE
        getPostImages(post.postId).observe(c as AppCompatActivity, Observer {

            uiScope.launch {

                holder.postImages.adapter = PostImageAdapter(it, c,1)

            }

        })





        firebaseRepo.loadUserInfo(
            post.publisher, holder.profileImage, null, holder.userName, null, c)
        firebaseRepo.isLike(post.postId, holder.likeBtn)
        firebaseRepo.numberOfLikes(holder.likes, post.postId)
        firebaseRepo.numberOfComments(holder.comments, post.postId)
        firebaseRepo.checkSavedStatus(post.postId, holder.saveBtn)

        holder.postDescription.setOnClickListener {
            if (postPage == 1) {
                homeToPostDetail(it, post.postId, post.publisher)
            } else if (postPage == 2) {
                profileToPostDetail(it, post.postId, post.publisher)
            } else {
                starredToPostDetail(it, post.postId, post.publisher)
            }
        }

        holder.itemView.setOnClickListener {
            if (postPage == 1) {
                homeToPostDetail(it, post.postId, post.publisher)
            } else if (postPage == 2) {
                profileToPostDetail(it, post.postId, post.publisher)
            } else
                starredToPostDetail(it, post.postId, post.publisher)
        }

        holder.profileImage.setOnClickListener {

            if (postPage == 1) {
                homeToProfile(it, post.publisher)
            } else if (postPage == 3) {
                starredToProfile(it, post.publisher)
            }
        }

        holder.userName.setOnClickListener {

            if (postPage == 1) {
                homeToProfile(it, post.publisher)
            } else if (postPage == 3) {
                starredToProfile(it, post.publisher)
            }
        }

        holder.likeBtn.setOnClickListener {

            firebaseRepo.like(holder.likeBtn, post.postId, post.publisher)
        }

        holder.commentBtn.setOnClickListener {
            if (postPage == 1) {
                homeToPostDetail(it, post.postId, post.publisher)
            } else if (postPage == 2) {
                profileToPostDetail(it, post.postId, post.publisher)
            } else starredToPostDetail(it, post.postId, post.publisher)
        }

        holder.comments.setOnClickListener {
            if (postPage == 1) {
                homeToPostDetail(it, post.postId, post.publisher)
            } else if (postPage == 2) {
                profileToPostDetail(it, post.postId, post.publisher)
            } else
                starredToPostDetail(it, post.postId, post.publisher)
        }

        holder.saveBtn.setOnClickListener {

            firebaseRepo.save(holder.saveBtn, post.postId)

        }

        holder.likes.setOnClickListener {

            if (postPage == 1) {
                val action = HomeFragmentDirections
                    .actionNavHomeToNavShowusers("Likes", post.postId)
                it.findNavController().navigate(action)
            } else if (postPage == 2) {
                val action = ProfileFragmentDirections
                    .actionNavProfileToNavShowusers("Likes", post.postId)
                it.findNavController().navigate(action)
            } else {
                val action = StarredPostFragmentDirections
                    .actionNavStarredPostToNavShowusers("Likes", post.postId)
                it.findNavController().navigate(action)
            }

        }


    }


    fun getPostImages(postId: String): MutableLiveData<MutableList<PostImage>> {
        val l_postImages = MutableLiveData<MutableList<PostImage>>()
        firebaseRepo.getPostImages(postId, l_postImages)
        return l_postImages
    }


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var profileImage: ImageView = v.findViewById(R.id.post_user_profile_image)
        var likeBtn: ImageView = v.findViewById(R.id.post_like_btn)
        var postTime: TextView = v.findViewById(R.id.post_time)
        var commentBtn: ImageView = v.findViewById(R.id.post_comment_btn)
        var saveBtn: ImageView = v.findViewById(R.id.post_save_btn)
        var userName: TextView = v.findViewById(R.id.post_user_name)
        var likes: TextView = v.findViewById(R.id.post_like_text)
        var comments: TextView = v.findViewById(R.id.post_comment_text)
        var postDescription: TextView = v.findViewById(R.id.post_description)

        var postImages: RecyclerView = v.findViewById(R.id.post_uploaded_post_images)

    }

    private fun homeToPostDetail(v: View, postId: String, publisher: String) {

        val action = HomeFragmentDirections
            .actionNavHomeToPostDetailFragment(postId, publisher)
        v.findNavController().navigate(action)

    }

    private fun profileToPostDetail(v: View, postId: String, publisher: String) {

        val action = ProfileFragmentDirections
            .actionNavProfileToPostDetailFragment(postId, publisher)
        v.findNavController().navigate(action)

    }

    private fun starredToPostDetail(v: View, postId: String, publisher: String) {

        val action = StarredPostFragmentDirections
            .actionNavStarredPostToPostDetailFragment(postId, publisher)
        v.findNavController().navigate(action)

    }

    private fun homeToProfile(v: View, publisher: String) {

        val action = HomeFragmentDirections
            .actionNavHomeToNavProfile(publisher)
        v.findNavController().navigate(action)

    }

    private fun starredToProfile(v: View, publisher: String) {

        val action = StarredPostFragmentDirections
            .actionNavStarredPostToNavProfile(publisher)
        v.findNavController().navigate(action)

    }
}






