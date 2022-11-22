package com.bignerdranch.android.passwordmanager

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                passwordListViewModel.passwords.collect { passwords ->
                    binding.passwordRecyclerView.adapter = PasswordListAdapter(passwords) { passwordId ->
                        searchView?.clearFocus()

                        // Decryption

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_password_list, menu)

        val searchEntry: MenuItem = menu.findItem(R.id.menu_item_search)

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

    private fun showNewPassword() {
        searchView?.clearFocus()
        viewLifecycleOwner.lifecycleScope.launch {
            val newPassword = Password(
                id = UUID.randomUUID(),
                title = "",
                email = "",
                username = "",
                password = "",
                iv = "".toByteArray(),
                accessDate = Date(),
            )

            passwordListViewModel.addPassword(newPassword)
            findNavController().navigate(
                PasswordListFragmentDirections.showPasswordEntry((newPassword.id))
            )
        }
    }


}