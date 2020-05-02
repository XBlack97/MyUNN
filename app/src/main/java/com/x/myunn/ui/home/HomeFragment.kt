package com.x.myunn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.x.myunn.R
import com.x.myunn.adapter.PostAdapter


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    lateinit var recyclerView: RecyclerView

    lateinit var swipeRefresh: SwipeRefreshLayout

    lateinit var postAdapter: PostAdapter


    val viewModelFactory = HomeViewModelFactory()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bvn = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bvn.visibility = View.VISIBLE


//        val application = requireNotNull(this.activity).application
//
//        val dataSource = cwDatabase.getInstance(application).DatabaseDao

        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)




        recyclerView = view.findViewById(R.id.post_recycler_view)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager


        // Observe the model
        homeViewModel.postList.observe(this.viewLifecycleOwner, Observer { posts ->
            // Data bind the recycler view

            postAdapter = PostAdapter(requireContext(), posts, false)
            recyclerView.adapter = postAdapter
            postAdapter.notifyDataSetChanged()

            homeViewModel.myRef.keepSynced(true)
        })

        swipeRefresh = view.findViewById(R.id.swiperefresh)

        swipeRefresh.setOnRefreshListener {
            //homeViewModel.refresh(view!!)

            Toast.makeText(context, " Adapter size: ${postAdapter.itemCount}", Toast.LENGTH_LONG)
                .show()
            println("list: ")

            swipeRefresh.isRefreshing = false
        }

        return view
    }


}