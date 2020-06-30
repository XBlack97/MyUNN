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
import com.x.myunn.R
import com.x.myunn.adapter.CommentAdapter
import com.x.myunn.adapter.PostImageAdapter
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

    lateinit var commentAdapter: CommentAdapter

    private lateinit var viewModel: PostDetailViewModel
    lateinit var viewModelFactory: PostDetialViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val safeArgs: PostDetailFragmentArgs by navArgs()
        publisher = safeArgs.publisherId
        postId = safeArgs.postId


        view.postdetail_go_back.setOnClickListener { requireActivity().onBackPressed() }

        firebaseRepo.loadUserInfo(publisher, null, null, view.postdetail_toolbar_title, null, null)


        viewModelFactory = PostDetialViewModelFactory(requireContext(), safeArgs)

        recyclerViewComment = view.findViewById(R.id.recycler_view_comments)

        val linearLayout_c = LinearLayoutManager(context)
        linearLayout_c.reverseLayout = false
        linearLayout_c.stackFromEnd = true
        recyclerViewComment.layoutManager = linearLayout_c

        view.postdetail_uploaded_post_images.setHasFixedSize(true)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(PostDetailViewModel::class.java)


        viewModel.post.observe(this.viewLifecycleOwner, Observer {

//            if (it._isPostImages){
//                requireView().postdetail_uploaded_post_images.visibility = View.GONE
//            }else requireView().postdetail_uploaded_post_images.visibility = View.VISIBLE

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

        viewModel._postImages.observe(this.viewLifecycleOwner, Observer {


            Log.d("TAG-PD", "     PostDetialsImagesList: ${it}")

            val adapter = PostImageAdapter(it, requireContext(), 1)

            Log.d("TAG-PD", "     PD********")


            requireView().postdetail_uploaded_post_images.adapter = adapter

            Log.d("TAG-PD", "     PD-----------")

        })

        viewModel.loadCurrentUserImage(requireView().profile_image_comment, requireContext())

        viewModel.commentList.observe(this.viewLifecycleOwner, Observer {
            // Data bind the recycler view

            commentAdapter = CommentAdapter(requireContext(), it)

            recyclerViewComment.adapter = commentAdapter
            //recyclerViewComment.scrollToPosition(it.size - 1)

            //requireView().nsv.fullScroll(ScrollView.FOCUS_DOWN)

            requireView().nsv.scrollTo(0, requireView().nsv.bottom)

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
