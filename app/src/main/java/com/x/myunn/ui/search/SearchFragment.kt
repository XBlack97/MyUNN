package com.x.myunn.ui.search

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.x.myunn.R
import com.x.myunn.adapter.UserAdapter
import com.x.myunn.model.User
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser = mutableListOf<User>()

    private val uiScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        userAdapter = context?.let { UserAdapter(it, mUser, true) }

        recyclerView?.adapter = userAdapter

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        requireView().search_text_search_frag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view!!.search_text_search_frag.text.toString() == "") {
                    recyclerView?.visibility = View.GONE
                } else {
                    recyclerView?.visibility = View.VISIBLE

                    //viewModel.retrieveUsers(view!!.search_text_search_frag, mUser, userAdapter)
                    viewModel.searchUser(s.toString().toLowerCase(), mUser, userAdapter)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        requireView().search_text_search_frag.showKeyboard()

        requireView().search_text_search_frag.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboardFrom(requireContext(), requireView())
            }
        }
    }

    fun EditText.showKeyboard() {
        if (requestFocus()) {
            (activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(this, SHOW_IMPLICIT)
            setSelection(text.length)
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
