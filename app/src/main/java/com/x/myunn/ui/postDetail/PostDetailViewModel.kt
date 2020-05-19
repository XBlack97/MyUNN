package com.x.myunn.ui.postDetail

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Comment
import com.x.myunn.model.Post

class PostDetailViewModel(c: Context, safeArgs : PostDetailFragmentArgs) : ViewModel() {

    val firebaseRepo = FirebaseRepo()

    val publisher = safeArgs.publisherId
    val postId = safeArgs.postId

    lateinit var postUsername: String

    var firebaseUser = FirebaseAuth.getInstance().currentUser!!

    var commentList = loadComments()

    var post = loadPost_()



    fun loadPost(post: Post,
                 img: ImageView, userName: TextView,
                 postDescription: TextView, postTime: TextView, likeBtn: ImageView,
                 likeTextView: TextView, commentsTextView: TextView, saveBtn: ImageView, c: Context
    ) {

        postDescription.text = post.postDescription
        postTime.text = post.PostTime

        firebaseRepo.loadUserInfo(post.publisher, img, null, userName, null, c)
        firebaseRepo.isLike(post.postId, likeBtn)
        firebaseRepo.numberOfLikes(likeTextView, post.postId)
        firebaseRepo.numberOfComments(commentsTextView, post.postId)
        firebaseRepo.checkSavedStatus(post.postId, saveBtn)

    }


    fun loadCurrentUserImage(img: ImageView, c: Context) {

        firebaseRepo.loadUserInfo(
            firebaseUser.uid, img, null,
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
        firebaseRepo.like(likeBtn, postId, publisher)
    }

    fun save(saveBtn: ImageView) {
        firebaseRepo.save(saveBtn, postId)
    }

}
