package com.bignerdranch.android.passwordmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.passwordmanager.databinding.ListEntryPasswordBinding
import java.util.UUID

class PasswordListAdapter
    (private val passwords: List<Password>,
     private val onPasswordEntryClicked: (passwordId: UUID) -> Unit
) : RecyclerView.Adapter<PasswordHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : PasswordHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListEntryPasswordBinding.inflate(inflater, parent, false)
        return PasswordHolder(binding)
    }
    override fun onBindViewHolder(holder: PasswordHolder, position: Int) {
        val password = passwords[position]
        holder.bind(password, onPasswordEntryClicked)
    }
    override fun getItemCount() = passwords.size

}


class PasswordHolder(private val binding: ListEntryPasswordBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(password: Password, onPasswordEntryClicked: (passwordId: UUID) -> Unit){
        binding.passwordTitle.text = password.title
        binding.passwordEmail.text = displayEmail(password.email.toString())

        binding.root.setOnClickListener {
            // Decrypt
            onPasswordEntryClicked(password.id)
        }
    }

    private fun displayEmail(passwordEmail: String) : String{

        var result = passwordEmail.take(4)

        for (i in 1..12){
            result += "*"
        }

        return result
    }
}