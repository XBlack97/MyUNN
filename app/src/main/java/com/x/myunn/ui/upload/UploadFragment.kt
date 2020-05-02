package com.x.myunn.ui.upload

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.x.myunn.R
import kotlinx.android.synthetic.main.fragment_upload.view.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class UploadFragment : Fragment() {

    private lateinit var uploadViewModel: UploadViewModel

    private var uploadFragmentJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + uploadFragmentJob)


    var bnv: BottomNavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bnv = requireActivity().findViewById(R.id.nav_view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_upload, container, false)

//
//
//        val application = requireNotNull(this.activity).application
//
//        val dataSource = cwDatabase.getInstance(application).DatabaseDao
//
        val viewModelFactory = UploadViewModelFactory()

        uploadViewModel = ViewModelProvider(this, viewModelFactory).get(UploadViewModel::class.java)



        view.upload_post.setOnClickListener {
            uiScope.launch {
                uploadFeed(it)
            }
        }

        return view
    }

    fun onSNACK(view: View, text: String) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("Action", null)
        snackbar.setActionTextColor(ContextCompat.getColor(view.context, R.color.colorPrimaryDark))
        val snackbarView = snackbar.view
        val tv: TextView = snackbarView.findViewById(R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        snackbarView.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.colorPrimaryDark
            )
        )
        snackbar.duration = TimeUnit.SECONDS.toMillis(2).toInt()
        snackbar.show()


        uiScope.launch {
            delay(TimeUnit.SECONDS.toMillis(2))
            bnv?.visibility = View.VISIBLE
        }

    }

    fun uploadFeed(v: View) {

        val postDescription = requireView().post_text.text.toString()

        if (postDescription.isBlank()) {
            bnv!!.visibility = View.GONE
            onSNACK(v, "empty field !!")

        } else {

            uploadViewModel.uploadPost(postDescription)
            findNavController().navigate(R.id.action_nav_upload_to_nav_home)
            Toast.makeText(requireContext(), "Uploaded Sucessfully", Toast.LENGTH_LONG).show()

        }

    }
}

