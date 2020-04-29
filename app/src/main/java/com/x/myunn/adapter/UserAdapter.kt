package com.x.myunn.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.x.myunn.R
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.User
import com.x.myunn.ui.profile.ProfileFragment
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var c: Context,
    private var mUser: MutableList<User>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    val firebaseRepo = FirebaseRepo()

    var navController =
        Navigation.findNavController((c as FragmentActivity), R.id.nav_host_fragment)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.user_item_list_view, parent, false)


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.username.text = "@${user.username}"
        holder.fullname.text = user.fullname

        Glide.with(c)
            .load(user.image)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(holder.userImage)

        firebaseRepo.checkFollowingAndFollowButtonStatus(user.uid, holder.follow_btn)

        holder.itemView.setOnClickListener {
            if (isFragment) {
                val pref = c.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.uid)
                pref.apply()

                (c as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, ProfileFragment())
                    .addToBackStack(null)
                    .commit()

                //navController.navigate(R.id.action_showUsersFragment_to_nav_profile)

            } else {

//                val intent = Intent(c, MainActivity::class.java)
//                intent.putExtra("publisherId", user.uid)
//                c.startActivity(intent)
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
        var userImage: CircleImageView = v.findViewById(R.id.user_profileImage)
        var follow_btn: Button = v.findViewById(R.id.user_followButton)

    }
}