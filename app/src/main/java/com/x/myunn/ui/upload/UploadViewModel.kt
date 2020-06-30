package com.x.myunn.ui.upload


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.x.myunn.firebase.FirebaseRepo
import kotlinx.coroutines.Job

class UploadViewModel : ViewModel() {

    private var viewModelJob = Job()

    val firebaseRepo = FirebaseRepo()

    private val _navigateBackHome = MutableLiveData<Long>()
    val navigateBackHome
        get() = _navigateBackHome

    fun onUploadClicked(id: Long) {
        _navigateBackHome.value = id
    }

    fun onHomeNavigated() {
        _navigateBackHome.value = null
    }

    fun uploadPost(postDescription: String, imageList: ArrayList<String>?) {

        firebaseRepo.uploadPost(postDescription, imageList)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}



