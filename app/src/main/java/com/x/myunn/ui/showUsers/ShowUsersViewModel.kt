package com.x.myunn.ui.showUsers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.x.myunn.model.User

class ShowUsersViewModel(c: Context) : ViewModel() {


    val pref = c.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
    val id = pref.getString("id", "none").toString()

    var userList = mutableListOf<User>()
    var idList = mutableListOf<String>()


    var l_userList = MutableLiveData<MutableList<User>>()

    val userLists = showUsers(l_userList)


    fun getFollowers() {


        val followersRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id).child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    idList.clear()

                    for (sp in p0.children) {
                        idList.add(sp.key!!)

                    }
                    showUsers(l_userList)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun getFollowing() {


        val followersRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id).child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    idList.clear()

                    for (sp in p0.children) {
                        idList.add(sp.key!!)

                    }
                    showUsers(l_userList)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun getLikes() {

        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes")
            .child(id)

        likesRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    idList.clear()

                    for (sp in p0.children) {
                        idList.add(sp.key!!)
                    }
                    showUsers(l_userList)
                }
            }
        })
    }

    fun showUsers(l_userList: MutableLiveData<MutableList<User>>): MutableLiveData<MutableList<User>> {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(ds: DataSnapshot) {
                userList.clear()

                for (snapshot in ds.children) {
                    val user = snapshot.getValue(User::class.java)

                    for (id in idList) {

                        if (user!!.uid == id) {
                            userList.add(user)

                        }
                    }
                }
                l_userList.value = userList

            }
        })

        return l_userList
    }

    fun myPost(profileId: String): MutableLiveData<MutableList<User>> {

        val l_postList = MutableLiveData<MutableList<User>>()

        // firebaseRepo.myPost(profileId, l_postList)

        return l_postList
    }

}
