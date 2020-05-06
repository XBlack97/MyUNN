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
import kotlinx.android.synthetic.main.sign_up_fragment.*
import kotlinx.android.synthetic.main.sign_up_fragment.view.*

class SignUpFragment : Fragment() {

    lateinit var fullname: String
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String
    lateinit var password2: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.sign_up_fragment, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().sign_up_sign_in.setOnClickListener {
            findNavController().navigate(R.id.action_SignUpFrag_to_SignInFrag)
        }

        requireView().signup.setOnClickListener {
            CreateAccount()
        }


    }

    private fun CreateAccount() {

        fullname = signup_fullname.text.toString()
        username = signup_username.text.toString()
        email = signup_email.text.toString()
        password = signup_password.text.toString()
        password2 = signup_confirm_password.text.toString()

        when {
            TextUtils.isEmpty(fullname) ->
                Toast.makeText(requireContext(), "Full Name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username) ->
                Toast.makeText(requireContext(), "Username is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) ->
                Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(requireContext(), "password Name is required", Toast.LENGTH_LONG)
                    .show()
            TextUtils.isEmpty(password2) ->
                Toast.makeText(requireContext(), "Confirm password empty", Toast.LENGTH_LONG).show()
            (password2 != password) ->
                Toast.makeText(requireContext(), "Confirm password incorrect", Toast.LENGTH_LONG)
                    .show()


            else -> {
                FirebaseRepo().CreateAccount(fullname, username, email, password, requireContext())
            }

        }
    }
}
