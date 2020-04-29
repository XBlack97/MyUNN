package com.x.myunn.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.adapter.NotificationAdapter
import com.x.myunn.model.Notification

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    private var nList = mutableListOf<Notification>()
    private var nAdapter: NotificationAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_notifications, container, false)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_notifications)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)



        notificationsViewModel.notifications.observe(viewLifecycleOwner, Observer {

            nAdapter = NotificationAdapter(requireContext(), it)
            recyclerView.adapter = nAdapter

        })
        return view
    }

}
