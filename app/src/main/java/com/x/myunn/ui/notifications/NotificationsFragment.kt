package com.x.myunn.ui.notifications

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
import com.x.myunn.adapter.NotificationAdapter

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    private var nAdapter: NotificationAdapter? = null

    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val bvn = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bvn.visibility = View.VISIBLE


        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        notificationsViewModel.notifications.observe(viewLifecycleOwner, Observer {

            nAdapter = NotificationAdapter(requireContext(), it)
            recyclerView.adapter = nAdapter

        })


    }

}
