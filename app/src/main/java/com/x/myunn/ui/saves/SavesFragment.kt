package com.x.myunn.ui.saves

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.x.myunn.R
import com.x.myunn.adapter.PostAdapter

class SavesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var postAdapter: PostAdapter

    private lateinit var savesViewModel: SavesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saves, container, false)

        val bvn = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bvn.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.recycler_view_star)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savesViewModel = ViewModelProvider(this).get(SavesViewModel::class.java)

        // Observe the model
        savesViewModel.savedPostList.observe(this.viewLifecycleOwner, Observer {
            // Data bind the recycler view

            postAdapter = PostAdapter(requireContext(), it, 3)
            recyclerView.adapter = postAdapter
            postAdapter.notifyDataSetChanged()

        })


    }

}
