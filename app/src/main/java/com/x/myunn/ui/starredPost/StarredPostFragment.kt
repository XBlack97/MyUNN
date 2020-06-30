package com.x.myunn.ui.starredPost

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.x.myunn.R
import com.x.myunn.adapter.PostAdapter
import kotlinx.android.synthetic.main.fragment_starred_post.view.*

class StarredPostFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var postAdapter: PostAdapter

    private lateinit var starredPostViewModel: StarredPostViewModel

    private val TAG = "TAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_starred_post, container, false)

        val bvn = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bvn.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.recycler_view_star)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        view.starred_go_back.setOnClickListener { requireActivity().onBackPressed() }

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        starredPostViewModel = ViewModelProvider(this).get(StarredPostViewModel::class.java)

        // Observe the model
        starredPostViewModel.savedPostList.observe(this.viewLifecycleOwner, Observer {
            // Data bind the recycler view

            postAdapter = PostAdapter(requireContext(), it, 3)
            recyclerView.adapter = postAdapter
            postAdapter.notifyDataSetChanged()


            Toast.makeText(requireContext(), "list: ${postAdapter.itemCount}", Toast.LENGTH_SHORT).show()

            checkEmpty()
        })

    }

    private fun checkEmpty() {
        if (postAdapter.itemCount == 0) {

            requireView().recycler_view_star.visibility = View.GONE
            requireView().all_caught_up_text.visibility = View.VISIBLE
        } else {
            requireView().recycler_view_star.visibility = View.VISIBLE
            requireView().all_caught_up_text.visibility = View.GONE
        }

    }

}
