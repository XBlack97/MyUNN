package com.x.myunn.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class HomeViewModel : ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    //internal val newsFeed = databaseDao.loadAllFeeds()


    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Posts")

    val firebaseRepo = FirebaseRepo()

    var postList = loadPosts()


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

//    fun refresh(v: View) {
//        //Toast.makeText(v.context, "Refreshing...", Toast.LENGTH_SHORT).show()
//
//        uiScope.launch {
//            withContext(Dispatchers.IO) {
//                //val getFeeds = databaseDao.checkFeeds()
//                withContext(Dispatchers.Main) {
//                    checkFeeds(v, firelist)
//                }
//            }
//        }
//
//
//    }
//
//
//    fun checkFeeds(v: View, feeds: MutableList<cw1>) {
//
//        if (feeds.isNullOrEmpty()) {
//            Toast.makeText(v.context, "Database is empty", Toast.LENGTH_SHORT).show()
//
//            loadfirebase()
//
//        } else {
//            Toast.makeText(
//                v.context,
//                "number of entries : ${feeds.count()}",
//                Toast.LENGTH_SHORT
//            )
//                .show()
//        }
//    }

    fun loadPosts(): MutableLiveData<MutableList<Post>> {

        val l_postList = MutableLiveData<MutableList<Post>>()

        firebaseRepo.retrievePosts(l_postList)

        return l_postList
    }

//    fun getPostImages(): MutableLiveData<MutableList<PostImage>> {
//        val l_postImages = MutableLiveData<MutableList<PostImage>>()
//        firebaseRepo.getPostImages(postId, l_postImages)
//        return l_postImages
//    }


//    fun clear() {
//            uiScope.launch {
//                withContext(Dispatchers.IO) {
//                    databaseDao.clearAllFeeds()
//                }
//            }
//        }
}
