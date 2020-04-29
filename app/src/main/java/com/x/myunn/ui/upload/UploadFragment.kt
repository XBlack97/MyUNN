package com.x.unncrimewatch_k.ui.uploadFeed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.x.myunn.R
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class UploadFragment : Fragment() {

    private lateinit var uploadViewModel: UploadViewModel

    private var uploadFragmentJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + uploadFragmentJob)

    lateinit var navController: NavController

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

        uploadViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(UploadViewModel::class.java)


        navController = findNavController(requireActivity(), R.id.nav_host_fragment)

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

        val postDescription = post_text.text.toString()

        if (postDescription.isBlank()) {
            bnv!!.visibility = View.GONE
            onSNACK(v, "empty field !!")

        } else {

            uploadViewModel.uploadPost(v, postDescription)

            navController.navigate(R.id.action_uploadFragment_to_navigation_home)
        }

    }
}

