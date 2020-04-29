package com.x.myunn.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.x.myunn.R
import com.x.myunn.activities.LogInActivity
import com.x.myunn.adapter.PostAdapter
import com.x.myunn.ui.showUsers.ShowUsersFragment
import com.x.unncrimewatch_k.ui.home.ProfileViewModelFactory
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_info.*
import kotlinx.android.synthetic.main.profile_info.view.*

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var viewModel: ProfileViewModel
    private lateinit var profifeRecyclerView: RecyclerView

    lateinit var swipeRefresh: SwipeRefreshLayout

    lateinit var postAdapter: PostAdapter

    lateinit var viewModelFactory: ProfileViewModelFactory

    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        } else {
            Toast.makeText(requireContext(), "profileId: null", Toast.LENGTH_LONG).show()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        navController =
            Navigation.findNavController((context as FragmentActivity), R.id.nav_host_fragment)


        setHasOptionsMenu(true)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        viewModelFactory = ProfileViewModelFactory(requireContext())


        swipeRefresh = view.findViewById(R.id.profile_swipeRefresh)

        profifeRecyclerView = view.findViewById(R.id.profile_recycler_view)
        profifeRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        profifeRecyclerView.layoutManager = linearLayoutManager


        val bnv = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

//        profifeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0 || dy < 0) {
//                    bnv.visibility = View.GONE
//                }
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    bnv.visibility = View.VISIBLE
//                }
//
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//        })


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
//            val intent = Intent(context, ShowUsersFragment::class.java)
//            intent.putExtra("id", profileId)
//            intent.putExtra("title", "followers")
//            startActivity(intent)

            val editor =
                (context as FragmentActivity).getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                    .edit()
            editor.putString("id", profileId)
            editor.putString("title", "Followers")
            editor.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ShowUsersFragment())
                .addToBackStack(null)
                .commit()

            //navController.navigate(R.id.action_nav_profile_to_showUsersFragment)
        }

        view.followingsCounter.setOnClickListener {
//            val intent = Intent(context, ShowUsersActivity::class.java)
//            intent.putExtra("id", profileId)
//            intent.putExtra("title", "following")
//            startActivity(intent)

            val editor =
                (context as FragmentActivity).getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                    .edit()
            editor.putString("id", profileId)
            editor.putString("title", "Following")
            editor.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ShowUsersFragment())
                .addToBackStack(null)
                .commit()

            //navController.navigate(R.id.action_nav_profile_to_showUsersFragment)
        }




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    navController.navigate(R.id.action_nav_profile_to_profileSettingFragment)
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

        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)


        // Observe the model
        viewModel.postList.observe(this.viewLifecycleOwner, Observer { posts ->
            // Data bind the recycler view


            postAdapter = PostAdapter(requireContext(), posts)
            profifeRecyclerView.adapter = postAdapter
            postAdapter.notifyDataSetChanged()

            //viewModel.myRef.keepSynced(true)
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
            viewModel.checkFollowingAndFollowButtonStatus(profileId, followButton)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.profile_menu, menu)
    }


    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()

    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}

