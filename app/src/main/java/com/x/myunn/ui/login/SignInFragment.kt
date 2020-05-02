package com.x.myunn.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.x.myunn.R
import com.x.myunn.firebase.FirebaseRepo
import kotlinx.android.synthetic.main.sign_in_fragment.view.*

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.sign_up_new.setOnClickListener {

            findNavController().navigate(R.id.action_SignInFrag_to_SignUpFrag)
        }

        view.signin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        //val firebaseRepo = FirebaseRepo()

        val email = requireView().signin_email.text.toString()
        val password = requireView().signin_password.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(
                context,
                "Email is required",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                context,
                "password is required",
                Toast.LENGTH_LONG
            ).show()
            else -> {

                FirebaseRepo().loginUser(email, password, requireContext())
            }
        }
    }
}
