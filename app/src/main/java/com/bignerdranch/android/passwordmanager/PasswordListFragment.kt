package com.bignerdranch.android.passwordmanager

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.passwordmanager.PasswordListFragmentDirections
import com.bignerdranch.android.passwordmanager.databinding.FragmentPasswordListBinding
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.log

private const val TAG = "PasswordListFragment"

class PasswordListFragment : Fragment() {

    private val passwordListViewModel: PasswordListViewModel by viewModels()
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    private var _binding: FragmentPasswordListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPasswordListBinding.inflate(inflater, container, false)

        binding.passwordRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "ViewCreated: ${searchView?.query}")


        binding.apply {
            emptyListButton.setOnClickListener {
                showNewPassword()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                passwordListViewModel.passwords.collect { passwords ->
                    if(passwords.isEmpty()){
                        binding.emptyListButton.visibility = View.VISIBLE
                        binding.listEmptyText.visibility = View.VISIBLE
                        binding.passwordRecyclerView.visibility = View.GONE
                    }
                    else{
                        binding.emptyListButton.visibility = View.GONE
                        binding.listEmptyText.visibility = View.GONE
                        binding.passwordRecyclerView.visibility = View.VISIBLE
                    }

                    binding.passwordRecyclerView.adapter = PasswordListAdapter(passwords) { passwordId ->
                        passwordListViewModel.setQuery("")
                        searchView?.setQuery("", true)
                        searchView?.clearFocus()

                        findNavController().navigate(
                            PasswordListFragmentDirections.showPasswordEntry(passwordId)
                        )
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        println("LIST DESTROYED")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_password_list, menu)

        val searchEntry: MenuItem = menu.findItem(R.id.menu_item_search)

        Log.d(TAG, "HERE: ${searchView?.query}")

        searchView = searchEntry.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //passwordListViewModel.setQuery(query ?: "")
                searchView?.clearFocus()
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                passwordListViewModel.setQuery(query ?: "")
                Log.d(TAG, "QUERY: $query")
                return true
            }
        })
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_password -> {
                searchView?.clearFocus()
                showNewPassword()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()

        Log.d(TAG, "MENU DESTROYED")
        searchView?.setQuery("", true)
        passwordListViewModel.setQuery("")
    }

    private fun showNewPassword() {
        searchView?.clearFocus()
        viewLifecycleOwner.lifecycleScope.launch {
            val newPassword = Password(
                id = UUID.randomUUID(),
                title = "",
                email = "",
                username = "",
                cipherText = "",
                password = "".toByteArray(),
                iv = "".toByteArray(),
                accessDate = Date(),
            )
            println("Just Created INITIAL V :" + newPassword.iv)

            passwordListViewModel.addPassword(newPassword)
            findNavController().navigate(
                PasswordListFragmentDirections.showPasswordEntry((newPassword.id))
            )
        }
    }

}