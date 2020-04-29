package com.x.myunn.firebase

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.x.myunn.R
import com.x.myunn.activities.MainActivity
import com.x.myunn.adapter.CommentsAdapter
import com.x.myunn.adapter.UserAdapter
import com.x.myunn.model.Comment
import com.x.myunn.model.Notification
import com.x.myunn.model.Post
import com.x.myunn.model.User
import com.x.unncrimewatch_k.firebase.FirebaseHandler
import com.x.unncrimewatch_k.ui.uploadFeed.UploadFragment
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FirebaseRepo {

    val mAuth = FirebaseAuth.getInstance()
    val ref = FirebaseDatabase.getInstance().reference
    val storageProfilePicRef = FirebaseStorage.getInstance().reference
        .child("Profile Pictures")

    init {
        FirebaseHandler()
    }


    fun CreateAccount(
        fullname: String, username: String, email: String,
        password: String, c: Context
    ) {

        val pd = ProgressDialog(c)
        pd.setTitle("SignUp")
        pd.setMessage("Creating Account, please wait...")
        pd.setCanceledOnTouchOutside(false)
        pd.show()


        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    saveUsernameInfo(fullname, username, email, pd, c)
                } else {
                    val errorMsg = it.exception!!.toString()
                    Toast.makeText(c, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    pd.dismiss()
                }
            }

    }

    fun saveUsernameInfo(
        fullname: String, username: String, email: String,
        pd: ProgressDialog, c: Context
    ) {

        val currentUserID = mAuth.currentUser!!.uid
        val userRef = ref.child("Users")

        val userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserID
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["email"] = email
        userMap["bio"] = ""
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/myunn-74e19.appspot." +
                "com/o/Default%20image%2Fprofile.png?alt=media&token=2c79543b-6a94-46df-bdc4-4b22c47a6079"

        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    pd.dismiss()
                    Toast.makeText(c, "Account created succefully", Toast.LENGTH_LONG).show()

//                    ref.child("Follow")
//                        .child(currentUserID).child("following").child(currentUserID).setValue(true)

                    val intent = Intent(c, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    c.startActivity(intent)

                } else {
                    val errorMsg = it.exception!!.toString()
                    Toast.makeText(c, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    pd.dismiss()
                }
            }
    }

    fun loginUser(email: String, password: String, c: Context) {

        val pd = ProgressDialog(c)
        pd.setTitle("Sign in")
        pd.setMessage("Signing in...")
        pd.setCanceledOnTouchOutside(false)
        pd.show()


        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    pd.dismiss()
                    Toast.makeText(c, "Signed in", Toast.LENGTH_LONG).show()

                    val intent = Intent(c, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    c.startActivity(intent)
                } else {
                    val errorMsg = it.exception!!.toString()
                    Toast.makeText(c, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    pd.dismiss()
                }
            }

    }

    fun loadUserInfo(
        profileId: String?,
        img: CircleImageView?,
        userImage: String?,
        fullname: TextView?,
        username: TextView?,
        bio: TextView?,
        c: Context?
    ) {

        val userRef =
            ref.child("Users").child(profileId!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(ds: DataSnapshot) {

                if (ds.exists()) {

                    val user = ds.getValue(User::class.java)!!

                    if (c != null) {
                        Glide.with(c)
                            .load(user.image)
                            .apply(
                                RequestOptions()
                                    .placeholder(R.drawable.loading_animation)
                                    .error(R.drawable.ic_broken_image)
                            )
                            .into(img!!)
                    }


                    if (userImage != null) {
                        user.image = userImage
                    }

                    username?.text = user.username
                    fullname?.text = user.fullname
                    bio?.text = user.bio

                }

            }

            override fun onCancelled(p0: DatabaseError) {}
        })

    }


    fun updateImageUserInfo(
        fullname: String,
        username: String,
        bio: String,
        imageUri: Uri,
        c: Context
    ) {

        val pd = ProgressDialog(c)
        pd.setTitle("Updating")
        pd.setMessage("Updating account...")
        pd.show()

        val fileRef = storageProfilePicRef.child(mAuth.currentUser!!.uid + "jpg")

        val uploadTask = fileRef.putFile(imageUri)

        uploadTask.continueWith { task ->
            if (!task.isSuccessful) {
                pd.dismiss()
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result!!.addOnSuccessListener { it ->
                        println("Main: $it")
                        val myUri = it.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = fullname
                        userMap["username"] = username
                        userMap["bio"] = bio
                        userMap["image"] = myUri

                        ref.child(mAuth.currentUser!!.uid).updateChildren(userMap)

                        Toast.makeText(
                            c,
                            "Account info updated succefully",
                            Toast.LENGTH_LONG
                        ).show()

                        pd.dismiss()
                    }

                } else {
                    pd.dismiss()
                }
            }

    }

    fun updateUserInfoOnly(fullname: String, username: String, bio: String, c: Context) {

        val userRef = ref.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["bio"] = bio

        userRef.child(mAuth.currentUser!!.uid).updateChildren(userMap)

        Toast.makeText(c, "Account info updated succefully", Toast.LENGTH_LONG).show()

        val intent = Intent(c, MainActivity::class.java)
        c.startActivity(intent)
    }

    fun retrievePosts(postList: MutableLiveData<MutableList<Post>>) {

        val postsRef = ref.child("Posts")
        var mPostList = mutableListOf<Post>()

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                mPostList.clear()

                for (snapshot in p0.children) {
                    val post = snapshot.getValue(Post::class.java)

                    mPostList.add(post!!)


                }

                postList.value = mPostList
            }
        })
    }


    fun uploadPost(v: View, postDescription: String) {

        val postRef = ref.child("Posts")

        val sdf = SimpleDateFormat("HH:mm, MMM d, yyyy ", Locale.US)
        val now = sdf.format(Calendar.getInstance().time)

        val postId = postRef.push().key

        val postMap = HashMap<String, Any>()
        postMap["postId"] = postId!!
        postMap["postTime"] = now
        postMap["postDescription"] = postDescription
        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid

        postRef.child(postId).updateChildren(postMap)


        val uploadFragment = UploadFragment()
        uploadFragment.onSNACK(v, "Uploaded Successfully")

    }

    fun getPost(postId: String, postList: MutableList<Post>) {
        val postRef =
            ref.child("Posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                postList.clear()

                val post = p0.getValue(Post::class.java)
                if (post != null) {
                    postList.add(post)
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun myPost(profileId: String, postList: MutableLiveData<MutableList<Post>>) {

        val postRef = ref.child("Posts")
        var mPostList = mutableListOf<Post>()

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    mPostList.clear()

                    for (sp in p0.children) {
                        val post = sp.getValue(Post::class.java)

                        if (post!!.publisher.equals(profileId)) {

                            mPostList.add(post)
                        }

                    }
                    postList.value = mPostList
                }
            }
        })
    }

    fun getTotalPosts(profileId: String, total_posts: TextView) {

        val postRef = ref.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var postNum = 0
                    for (sp in p0.children) {
                        val post = sp.getValue(Post::class.java)

                        if (post!!.publisher == profileId) {
                            postNum++
                        }
                    }

                    total_posts.text = postNum.toString() + " \n posts"
                }
            }
        })
    }

    fun getTotalFollowers(profileId: String, total_followers: TextView?) {

        val followersRef = ref.child("Follow")
            .child(profileId).child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(ds: DataSnapshot) {
                if (ds.exists()) {
                    total_followers?.text = ds.childrenCount.toString() + "\n followers"
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun getTotalFollowing(profileId: String, total_following: TextView?) {

        val followersRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(profileId).child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(ds: DataSnapshot) {
                if (ds.exists()) {
                    total_following?.text = ds.childrenCount.toString() + "\n following"
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun checkFollowingAndFollowButtonStatus(profileId: String, followBtn: Button) {

        val followingRef = mAuth.currentUser!!.uid.let {
            ref.child("Follow")
                .child(it).child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(ds: DataSnapshot) {

                if (ds.child(profileId).exists()) {
                    followBtn.text = "Following"
                } else {
                    followBtn.text = "Follow"
                }

            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun follow(profileId: String) {

        mAuth.currentUser!!.uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it).child("Following").child(profileId)
                .setValue(true)
        }

        mAuth.currentUser!!.uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(profileId).child("Followers").child(it)
                .setValue(true)
        }

        addNotificationFollow(profileId)

    }

    fun unFollow(profileId: String) {

        mAuth.currentUser!!.uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it.toString()).child("Following").child(profileId)
                .removeValue()
        }

        mAuth.currentUser!!.uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(profileId).child("Followers").child(it.toString())
                .removeValue()
        }

    }

    fun checkSavedStatus(postId: String, imageView: ImageView) {

        val saveRef = ref.child("Saves")
            .child(mAuth.currentUser!!.uid)

        saveRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_star_saved_24dp)
                    imageView.tag = "Saved"
                } else {
                    imageView.setImageResource(R.drawable.ic_star_unsaved_24dp)
                    imageView.tag = "Unsaved"
                }
            }
        })

    }

    fun isLike(postId: String, likeBtn: ImageView) {

        val likesRef = ref.child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(mAuth.currentUser!!.uid).exists()) {

                    likeBtn.setImageResource(R.drawable.ic_like_active)
                    likeBtn.tag = "Liked"
                } else {

                    likeBtn.setImageResource(R.drawable.ic_like)
                    likeBtn.tag = "Like"
                }
            }
        })

    }


    fun addComment(add_comment: TextView, postId: String, publisherId: String) {

        val commentsRef = ref.child("Comments").child(postId)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["publisher"] = mAuth.currentUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotificationComment(publisherId, postId, add_comment)

        add_comment.text = ""
    }

    fun readComments(
        commentList: MutableList<Comment>,
        postId: String,
        commentAdapter: CommentsAdapter
    ) {

        val commentRef = ref.child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    commentList.clear()

                    for (sp in p0.children) {
                        val comment = sp.getValue(Comment::class.java)

                        commentList.add(comment!!)
                    }
                    commentAdapter.notifyDataSetChanged()

                }
            }
        })
    }

    fun addNotificationComment(publisherId: String, postId: String, add_comment: TextView) {
        val notifyRef = ref.child("Notifications").child(publisherId)

        val nMap = HashMap<String, Any>()
        nMap["userId"] = mAuth.currentUser!!.uid
        nMap["text"] = "commented: " + add_comment.text.toString()
        nMap["postId"] = postId
        nMap["Post"] = true

        notifyRef.push().setValue(nMap)

    }

    fun addNotificationLike(userId: String, postId: String) {
        val notifyRef = ref.child("Notifications").child(userId)

        val nMap = HashMap<String, Any>()
        nMap["userId"] = mAuth.currentUser!!.uid
        nMap["text"] = "liked your post"
        nMap["postId"] = postId
        nMap["Post"] = true

        notifyRef.push().setValue(nMap)


    }

    fun addNotificationFollow(profileId: String) {
        val notifyRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(profileId)

        val nMap = java.util.HashMap<String, Any>()
        nMap["userId"] = mAuth.currentUser!!.uid
        nMap["text"] = "started following you"
        nMap["postId"] = ""
        nMap["Post"] = false

        notifyRef.push().setValue(nMap)


    }


    fun readNotifications(notifications: MutableLiveData<MutableList<Notification>>) {

        val notifyRef = ref.child("Notifications").child(mAuth.currentUser!!.uid)

        var mNotifications = mutableListOf<Notification>()

        notifyRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    mNotifications.clear()

                    for (sp in p0.children) {
                        val notification = sp.getValue(Notification::class.java)

                        mNotifications.add(notification!!)

                    }

                    Collections.reverse(mNotifications)
                    notifications.value = mNotifications

                }

            }
        })
    }

    fun retrieveUsers(_text: TextView?, users: MutableList<User>?, userAdapter: UserAdapter?) {

        val userRef = ref.child("Users")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(ds: DataSnapshot) {
                if (_text.toString() == "") {
                    users?.clear()

                    for (snapshot in ds.children) {
                        val user = snapshot.getValue(User::class.java)

                        //println("Retrieve Users : $user")

                        if (user != null) {
                            users?.add(user)
                        }
                    }
                    println("Retrieve Users : $users")
                    userAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    fun searchUser(s: String, users: MutableList<User>, userAdapter: UserAdapter?) {

        val query = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("fullname")
            .startAt(s)
            .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(ds: DataSnapshot) {
                users.clear()

                for (snapshot in ds.children) {
                    val user = snapshot.getValue(User::class.java)

                    //println("Search Users : $user")

                    if (user != null) {
                        users.add(user)
                        println("snapshot Users : $user")
                        println("Search Users : $users")
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }
        })

    }

}

