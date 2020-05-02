package com.x.myunn.ui.postDetail

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.x.myunn.adapter.PostAdapter
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Comment
import com.x.myunn.model.Post
import de.hdodenhof.circleimageview.CircleImageView

class PostDetailViewModel(c: Context, safeArgs : PostDetailFragmentArgs) : ViewModel() {

    val firebaseRepo = FirebaseRepo()

    val publisher = safeArgs.publisherId
    val postId = safeArgs.postId

    var firebaseUser = FirebaseAuth.getInstance().currentUser!!

    val postAdapter = PostAdapter.newInstance()

    var commentList = loadComments()

    var post = loadPost_()



    fun loadPost(post: Post,
        img: CircleImageView, userName: TextView,
        postDescription: TextView, postTime: TextView, likeBtn: ImageView,
        likeTextView: TextView, commentsTextView: TextView, saveBtn: ImageView, c: Context
    ) {

        postDescription.text = post.postDescription
        postTime.text = post.PostTime

        postAdapter.publisherInfo(img, userName, post.publisher, c)
        firebaseRepo.isLike(post.postId, likeBtn)
        postAdapter.numberOfLikes(likeTextView, post.postId)
        postAdapter.numberOfComments(commentsTextView, post.postId)
        firebaseRepo.checkSavedStatus(post.postId, saveBtn)

    }


    fun loadCurrentUserImage(img: CircleImageView, c: Context){

        firebaseRepo.loadUserInfo(
            firebaseUser.uid, img, null, null,
            null, null, c
        )
    }

    fun loadComments(): MutableLiveData<MutableList<Comment>> {

        val l_commentList = MutableLiveData<MutableList<Comment>>()

        firebaseRepo.readComments(postId, l_commentList)

        return l_commentList
    }

    fun loadPost_(): MutableLiveData<Post> {

        val l_post = MutableLiveData<Post>()

        firebaseRepo.getPost(postId, l_post)

        return l_post
    }

    fun addComment (add_comment: TextView, postId: String, publisher: String){
        firebaseRepo.addComment(add_comment, postId, publisher)
    }


    fun like(likeBtn: ImageView) {
        postAdapter.like(likeBtn, postId, publisher)
    }

    fun save(saveBtn: ImageView) {
        postAdapter.save(saveBtn, postId)
    }

}
