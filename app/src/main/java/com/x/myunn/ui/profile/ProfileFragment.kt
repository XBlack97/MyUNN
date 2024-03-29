package com.x.myunn.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.x.myunn.R
import com.x.myunn.activities.LogInActivity
import com.x.myunn.adapter.PostAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_profile_info.*
import kotlinx.android.synthetic.main.layout_profile_info.view.*

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    lateinit var profileId: String
    lateinit var profile_Id: String

    private lateinit var firebaseUser: FirebaseUser

    private lateinit var viewModel: ProfileViewModel
    private lateinit var profifeRecyclerView: RecyclerView

    lateinit var postAdapter: PostAdapter

    lateinit var viewModelFactory: ProfileViewModelFactory

    val TAG = "TAG"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

//        bnv = requireActivity().findViewById(R.id.nav_view)
//
//        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if(destination.id == R.id.nav_profile) {
//                bnv.visibility = View.VISIBLE
//                Log.d(TAG, "Profile: bnv visible")
//            } else {
//                bnv.visibility = View.VISIBLE
//            }
//        }


        Log.d(TAG, "Profile: onCreateView")


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        setHasOptionsMenu(true)

        val safeArgs: ProfileFragmentArgs by navArgs()
        profile_Id = safeArgs.profileId

        if (profile_Id == "user") {
            this.profileId = firebaseUser.uid
        } else {
            this.profileId = profile_Id
        }

        viewModelFactory = ProfileViewModelFactory(requireContext(), safeArgs)


        profifeRecyclerView = view.findViewById(R.id.profile_recycler_view)
        profifeRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        profifeRecyclerView.layoutManager = linearLayoutManager


        view.followButton.setOnClickListener {
            val getButtonText = view.followButton.text.toString()

            when (getButtonText) {
                "Follow" -> {

                    viewModel.follow(profileId)

                }
                "Following" -> {

                    viewModel.unFollow(profileId)

                }
            }


        }

        view.followersCounter.setOnClickListener {

            val id = profileId
            val title = "Followers"
            val action = ProfileFragmentDirections
                .actionNavProfileToNavShowusers(title, id)
            findNavController().navigate(action)

        }

        view.followingsCounter.setOnClickListener {
            val id = profileId
            val title = "Following"
            val action = ProfileFragmentDirections
                .actionNavProfileToNavShowusers(title, id)
            findNavController().navigate(action)

        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "Profile: onViewCreated")


        (activity as AppCompatActivity?)!!.setSupportActionBar(profile_toolbar)


        profile_toolbar.overflowIcon =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_menu_white_24dp)

        profile_toolbar.title = null

        profile_toolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.signOut -> {
                    Toast.makeText(requireContext(), "SignOut !!", Toast.LENGTH_LONG).show()
                    signOut()
                    true
                }
                R.id.editProfile -> {
                    Toast.makeText(requireContext(), "Edit Profile !!", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_nav_profile_to_nav_profilesetting)
                    true
                }
                R.id.starredPost -> {
                    findNavController().navigate(R.id.action_nav_profile_to_nav_starredPost)
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }


        }
    }

    private fun signOut() {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()

        val intent = Intent(context, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "Profile: onActivityCreated")


        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)

        // Observe the model
        viewModel.postList.observe(this.viewLifecycleOwner, Observer { posts ->
            // Data bind the recycler view

            postAdapter = PostAdapter(requireContext(), posts, 2)
            profifeRecyclerView.adapter = postAdapter
            postAdapter.notifyDataSetChanged()

            //viewModel.myRef.keepSynced(true)
            //Toast.makeText(requireContext(), "list: ${postAdapter.itemCount}", Toast.LENGTH_SHORT).show()


        })

        viewModel.loadUserInfo(
            profileId,
            profile_image,
            profile_fullname,
            profile_username,
            profile_bio,
            requireContext()
        )

        viewModel.getTotalPosts(profileId, postsCounter)

        viewModel.getTotalFollowers(profileId, followersCounter)

        viewModel.getTotalFollowing(profileId, followingsCounter)

        if (profileId == firebaseUser.uid) {
            requireView().followButton.visibility = View.GONE
        } else if (profileId != firebaseUser.uid) {
            requireView().followButton.visibility = View.VISIBLE
            viewModel.checkFollowingAndFollowButtonStatus(profileId, followButton)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.profile_menu, menu)
    }
}

