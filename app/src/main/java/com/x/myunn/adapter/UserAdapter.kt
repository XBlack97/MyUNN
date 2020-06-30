package com.x.myunn.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.activities.MainActivity
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.User
import com.x.myunn.ui.search.SearchFragmentDirections
import com.x.myunn.ui.showUsers.ShowUsersFragmentDirections
import com.x.myunn.utils.glideLoad


class UserAdapter(
    private var c: Context,
    private var mUser: MutableList<User>,
    private var isSearch: Boolean = false
) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{


    val firebaseRepo = FirebaseRepo()

    val main = MainActivity.newInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.item_layout_user, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.username.text = "@${user.username}"
        holder.fullname.text = user.fullname

        glideLoad(c, user.image, holder.userImage, false)

        firebaseRepo.checkFollowingAndFollowButtonStatus(user.uid, holder.follow_btn)

        holder.itemView.setOnClickListener {
            if (isSearch) {

                val profileId = user.uid
                val acton = SearchFragmentDirections.actionNavSearchToNavProfile(profileId)
                it.findNavController().navigate(acton)


            } else {

                val profileId = user.uid
                val acton = ShowUsersFragmentDirections.actionNavShowusersToNavProfile(profileId)
                it.findNavController().navigate(acton)
//
            }

        }

        holder.follow_btn.setOnClickListener {

            Log.d(TAG, "follow button clicked")

            if (holder.follow_btn.text.toString() == "Follow") {

                firebaseRepo.follow(user.uid)

            } else {
                firebaseRepo.unFollow(user.uid)
            }

        }

    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var username: TextView = v.findViewById(R.id.user_username)
        var fullname: TextView = v.findViewById(R.id.user_fullname)
        var userImage: ImageView = v.findViewById(R.id.user_profileImage)
        var follow_btn: Button = v.findViewById(R.id.user_followButton)

    }
}