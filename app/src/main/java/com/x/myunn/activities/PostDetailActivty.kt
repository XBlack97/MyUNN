package com.x.myunn.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.x.myunn.R
import com.x.myunn.adapter.CommentsAdapter
import com.x.myunn.adapter.PostAdapter
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Comment
import com.x.myunn.model.Post
import kotlinx.android.synthetic.main.activity_post_detail.*


class PostDetailActivty : AppCompatActivity() {

    lateinit var postId: String
    lateinit var publisher: String


    var commentList = mutableListOf<Comment>()

    var posts = mutableListOf<Post>()


    val firebaseRepo = FirebaseRepo()

    lateinit var recyclerViewPost: RecyclerView
    lateinit var recyclerViewComment: RecyclerView
    lateinit var postAdapter: PostAdapter
    lateinit var commentAdapter: CommentsAdapter

    var firebaseUser = FirebaseAuth.getInstance().currentUser!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        supportActionBar?.hide()

        val intent = intent
        postId = intent.getStringExtra("postId")!!
        publisher = intent.getStringExtra("publisherId")!!


        firebaseRepo.getPost(postId, posts)

        recyclerViewPost = findViewById(R.id.recycler_view_post)
        recyclerViewPost.setHasFixedSize(true)
        val linearlayout_p = LinearLayoutManager(this)
        recyclerViewPost.layoutManager = linearlayout_p

        postAdapter = PostAdapter(this, posts)
        recyclerViewPost.adapter = postAdapter


        firebaseRepo.loadUserInfo(
            firebaseUser.uid, profile_image_comment, null, null,
            null, null, this
        )


//        Glide.with(this)
//            .load(currentuserImage)
//            .apply(
//                RequestOptions()
//                    .placeholder(R.drawable.loading_animation)
//                    .error(R.drawable.ic_broken_image)
//            )
//            .into(profile_image_comment)


        recyclerViewComment = findViewById(R.id.recycler_view_comments)

        val linearLayout_c = LinearLayoutManager(this)
        linearLayout_c.reverseLayout = false
        linearLayout_c.stackFromEnd = true
        recyclerViewComment.layoutManager = linearLayout_c


        commentAdapter = CommentsAdapter(this, commentList)

        recyclerViewComment.adapter = commentAdapter
        recyclerViewComment.scrollToPosition(commentList.size - 1)

        firebaseRepo.readComments(commentList, postId, commentAdapter)





        post_comment.setOnClickListener {
            if (add_comment.text.toString() == "") {
                Toast.makeText(this, "pls write comment", Toast.LENGTH_LONG).show()
            } else {
                firebaseRepo.addComment(add_comment, postId, publisher)
            }
        }


    }


}


