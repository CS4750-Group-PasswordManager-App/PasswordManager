package com.bignerdranch.android.passwordmanager

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.M)
class PasswordViewFragment : Fragment() {

    //private lateinit var binding: FragmentPasswordViewBinding
    private var _binding: FragmentPasswordViewBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private var cryptoManager = CryptoManager()


    //private lateinit var password: Password

    private val args: PasswordViewFragmentArgs by navArgs()

    private val passwordEntryViewModel: PasswordEntryViewModel by viewModels {
        PasswordEntryViewModelFactory(args.passwordId)
    }

    private val passwordRepository = PasswordRepository.get()


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

                    //var cipher = cryptoManager.encrypt(text.toString())
                    oldPassword.copy(password = text.toString())
                }
                Log.d(TAG, "HERE")
            }

            saveEntryButton.setOnClickListener {
                // Save any updates and navigate back to list fragment
                //findNavController().navigate()
                passwordEntryViewModel.updatePassword { oldPassword ->
                    //Cipher Encrypt
                    var cipher = cryptoManager.encrypt(oldPassword.password)
                    oldPassword.copy(password = cipher.first.toString())
                    Log.d(TAG, oldPassword.toString())
                    Log.d(TAG, String(cipher.first))

                    oldPassword.copy(iv = cipher.second)

                }
                println("HERE")


            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                passwordEntryViewModel.password.collect { password ->
                    password?.let { updateUi(it) }

                    passwordEntryViewModel.updatePassword { oldPassword ->
                        oldPassword.copy(accessDate = Date())
                    }

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