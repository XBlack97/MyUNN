package com.x.myunn.ui.postDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.x.myunn.R
import com.x.myunn.adapter.CommentsAdapter
import com.x.myunn.firebase.FirebaseRepo
import kotlinx.android.synthetic.main.fragment_post_detail.view.*
import kotlinx.android.synthetic.main.layout_post_detail_counters_panel.view.*

class PostDetailFragment : Fragment() {

    companion object {
        fun newInstance() = PostDetailFragment()
    }

    lateinit var postId: String
    lateinit var publisher: String

    val firebaseRepo = FirebaseRepo()

    lateinit var recyclerViewComment: RecyclerView

    lateinit var commentAdapter: CommentsAdapter

    private lateinit var viewModel: PostDetailViewModel
    lateinit var viewModelFactory: PostDetialViewModelFactory

    lateinit var bnv: BottomNavigationView

    val TAG = "TAG"


    /**

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "PDF: onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "PDF: onStart")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "------------------")
        Log.d(TAG, "PDF: onAttach")
        bvn = requireActivity().findViewById(R.id.nav_view)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "PDF: onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "PDF: onResume")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "PDF: onViewStateRestored")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "PDF: onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "PDF: onDestroyView")

        bvn.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "PDF: onDestroy")
        bvn.visibility = View.VISIBLE
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "PDF: onDetach")
        Log.d(TAG, "------------------")
        bvn.visibility = View.VISIBLE
    }

     */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

//        bnv = requireActivity().findViewById(R.id.nav_view)
//
//        val navController = findNavController(requireActivity(),R.id.nav_host_fragment)// this maybe change
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if(destination.id == R.id.postDetailFragment) {
//                bnv.visibility = View.GONE
//            } else {
//                bnv.visibility = View.VISIBLE
//            }
//        }

        Log.d(TAG, "PDF: onCreateView")

        //bvn.visibility = View.GONE


        val safeArgs: PostDetailFragmentArgs by navArgs()
        publisher = safeArgs.publisherId
        postId = safeArgs.postId


        viewModelFactory = PostDetialViewModelFactory(requireContext(), safeArgs)

        recyclerViewComment = view.findViewById(R.id.recycler_view_comments)

        val linearLayout_c = LinearLayoutManager(context)
        linearLayout_c.reverseLayout = false
        linearLayout_c.stackFromEnd = true
        recyclerViewComment.layoutManager = linearLayout_c


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "PDF: onActivityCreated")

        viewModel = ViewModelProvider(this, viewModelFactory).get(PostDetailViewModel::class.java)


        viewModel.post.observe(this.viewLifecycleOwner, Observer {

            viewModel.loadPost(
                it,
                requireView().postdetail_user_profile_image,
                requireView().postdetail_user_name,
                requireView().postdetail_description,
                requireView().postdetail_time,
                requireView().postdetail_like_btn,
                requireView().postdetail_like_text,
                requireView().postdetail_comment_text,
                requireView().postdetail_save_btn,
                requireContext()
            )
        })

        viewModel.loadCurrentUserImage(requireView().profile_image_comment, requireContext())

        viewModel.commentList.observe(this.viewLifecycleOwner, Observer {
            // Data bind the recycler view

            commentAdapter = CommentsAdapter(requireContext(), it)

            recyclerViewComment.adapter = commentAdapter
            recyclerViewComment.scrollToPosition(it.size - 1)

        })

        requireView().post_comment.setOnClickListener {
            if (requireView().add_comment.text.toString() == "") {
                Toast.makeText(requireContext(), "pls write comment", Toast.LENGTH_LONG).show()
            } else {
                viewModel.addComment(requireView().add_comment, postId, publisher)
            }
        }

        requireView().postdetail_like_btn.setOnClickListener {
            viewModel.like(requireView().postdetail_like_btn)
        }

        requireView().postdetail_save_btn.setOnClickListener {
            viewModel.save(requireView().postdetail_save_btn)
        }

        requireView().postdetail_like_text.setOnClickListener {
            val action = PostDetailFragmentDirections
                .actionPostDetailFragmentToNavShowusers("Likes", postId)
            it.findNavController().navigate(action)
        }

        requireView().postdetail_user_profile_image.setOnClickListener {
            val action = PostDetailFragmentDirections
                .actionPostDetailFragmentToNavProfile(publisher)
            it.findNavController().navigate(action)
        }

        requireView().postdetail_user_name.setOnClickListener {
            val action = PostDetailFragmentDirections
                .actionPostDetailFragmentToNavProfile(publisher)
            it.findNavController().navigate(action)
        }

    }

}
