package com.x.myunn.ui.search

import androidx.lifecycle.ViewModel
import com.x.myunn.adapter.UserAdapter
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.User

class SearchViewModel : ViewModel() {

    val firebaseRepo = FirebaseRepo()

//    fun retrieveUsers(_text: TextView?, users: MutableList<User>?, userAdapter: UserAdapter?) {
//
//        firebaseRepo.getUsers(_text, users, userAdapter)
//
//    }

    fun searchUser(s: String, users: MutableList<User>, userAdapter: UserAdapter?) {

        firebaseRepo.searchUser(s, users, userAdapter)

    }
}
