package com.x.myunn.ui.showUsers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.adapter.UserAdapter
import kotlinx.android.synthetic.main.fragment_show_users.view.*

class ShowUsersFragment : Fragment() {

    companion object {
        fun newInstance() = ShowUsersFragment()
    }

    private lateinit var viewModel: ShowUsersViewModel

    lateinit var viewModelFactory: ShowUsersViewModelFactory

    var id: String = ""
    var title: String = ""

    lateinit var userAdapter: UserAdapter
    lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val safeArgs : ShowUsersFragmentArgs by navArgs()

        val id: String = safeArgs.id
        val title: String = safeArgs.tiltle

        this.title = title
        this.id = id


        viewModelFactory = ShowUsersViewModelFactory(requireContext(), safeArgs)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_users, container, false)

        view.show_users_title.text = title
        view.show_users_go_back.setOnClickListener { requireActivity().onBackPressed() }

        recyclerView = view.findViewById(R.id.show_users_recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ShowUsersViewModel::class.java)


        when (title) {

            "Likes" -> viewModel.getLikes()
            "Following" -> viewModel.getFollowing()
            "Followers" -> viewModel.getFollowers()
            //"views" -> getViews()

        }

        viewModel.userLists.observe(viewLifecycleOwner, Observer {

            userAdapter = UserAdapter(requireContext(), it, false)

            recyclerView.adapter = userAdapter

            userAdapter.notifyDataSetChanged()

        })

    }

    /** private fun getViews() {

    val viewsRef = FirebaseDatabase.getInstance().reference.child("Story")
    .child(id).child(intent.getStringExtra("storyId")!!).child("views")

    viewsRef.addValueEventListener(object: ValueEventListener {
    override fun onDataChange(p0: DataSnapshot) {
    if (p0.exists()) {

    (idList as ArrayList<String>).clear()

    for (sp in p0.children) {
    (idList as ArrayList<String>).add(sp.key!!)

    }
    showUsers()
    }
    }
    override fun onCancelled(p0: DatabaseError) {}
    })
    }
     */

}
