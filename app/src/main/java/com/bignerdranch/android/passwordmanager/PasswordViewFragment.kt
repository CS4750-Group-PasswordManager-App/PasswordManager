package com.bignerdranch.android.passwordmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.passwordmanager.databinding.FragmentPasswordViewBinding
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "PasswordViewFragment"

class PasswordViewFragment : Fragment() {

    //private lateinit var binding: FragmentPasswordViewBinding

    private var _binding: FragmentPasswordViewBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    //private lateinit var password: Password

    private val args: PasswordViewFragmentArgs by navArgs()

    private val passwordEntryViewModel: PasswordEntryViewModel by viewModels {
        PasswordEntryViewModelFactory(args.passwordId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPasswordViewBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            titleEntry.doOnTextChanged { text, _, _, _ ->
                //password = password.copy(title = text.toString())
                passwordEntryViewModel.updatePassword { oldPassword ->
                    oldPassword.copy(title = text.toString())
                }
            }

            emailEntry.doOnTextChanged { text, _, _, _ ->
                //password = password.copy(email = text.toString())
                passwordEntryViewModel.updatePassword { oldPassword ->
                    oldPassword.copy(email = text.toString())
                }
            }

            usernameEntry.doOnTextChanged { text, _, _, _ ->
                //password = password.copy(email = text.toString())
                passwordEntryViewModel.updatePassword { oldPassword ->
                    oldPassword.copy(username = text.toString())
                }
            }

            passwordEntry.doOnTextChanged { text, _, _, _ ->
                //password = password.copy(password = text.toString())
                passwordEntryViewModel.updatePassword { oldPassword ->
                    oldPassword.copy(password = text.toString())
                }
            }

            saveEntryButton.setOnClickListener {
                // Save any updates and navihate back to list fragment
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                passwordEntryViewModel.password.collect { password ->
                    password?.let { updateUi(it) }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUi(password: Password) {
        binding.apply {
            if (titleEntry.text.toString() != password.title){
                titleEntry.setText(password.title)
            }
            if (emailEntry.text.toString() != password.email){
                emailEntry.setText(password.email)
            }
            if (usernameEntry.text.toString() != password.username){
                usernameEntry.setText(password.username)
            }
            if (passwordEntry.text.toString() != password.password){
                passwordEntry.setText(password.password)
            }
        }

    }





}