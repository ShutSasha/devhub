package com.devhub.devhubapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.databinding.FragmentUserInfoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(layoutInflater, container, false)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())

        arguments?.let {
            val id = it.getString(ARG_ID) ?: ""
            val username = it.getString(ARG_USERNAME) ?: ""
            val avatarUrl = it.getString(ARG_AVATAR_URL) ?: ""
            val createdAt = it.getSerializable(ARG_CREATED_AT) as? Date ?: Date()
            val description = it.getString(ARG_DESCRIPTION) ?: ""
            val name = it.getString(ARG_NAME) ?: ""

            setUpFragment(id, username, avatarUrl, createdAt, description, name)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpFragment(
        id: String,
        username: String,
        avatarUrl: String,
        createdAt: Date,
        description: String,
        name: String
    ) {
        binding.tvUsername.text = username

        if (description.isNotEmpty()) {
            binding.tvDescription.text = description
            binding.tvDescription.visibility = View.VISIBLE
        } else {
            binding.tvDescription.visibility = View.GONE
        }

        if (name.isNotEmpty()) {
            binding.tvName.text = name
            binding.tvName.visibility = View.VISIBLE
        } else {
            binding.tvName.visibility = View.GONE
        }

        binding.tvFollowers.visibility = View.GONE

        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(createdAt)
        binding.tvDateOfJoining.text = "Joined on $formattedDate"

        avatarUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivAvatarPhoto)
        }

        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()


        if(encryptedPreferencesManager.getData("user_id") != id){
            binding.editBtnOutlined.visibility = View.GONE

            val primaryButtonFragment = PrimaryButtonFragment()
            primaryButtonFragment.setButtonText("Follow")
            primaryButtonFragment.setButtonAction {
                // Button action
            }
            fragmentTransaction.add(R.id.btnPrimary, primaryButtonFragment)

            val outlinedButtonFragment = OutlinedButtonFragment()
            outlinedButtonFragment.setButtonText("Send message")
            outlinedButtonFragment.setButtonAction {
                // Button action
            }
            fragmentTransaction.add(R.id.btnOutlined, outlinedButtonFragment)
        } else{
            binding.btnContainer.visibility = View.GONE

            val editOutlinedButtonFragment = OutlinedButtonFragment()
            editOutlinedButtonFragment.setButtonText("Edit profile")
            editOutlinedButtonFragment.setButtonAction {
                // Button action
            }
            fragmentTransaction.add(R.id.editBtnOutlined, editOutlinedButtonFragment)
        }

        fragmentTransaction.commit()
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_USERNAME = "username"
        private const val ARG_AVATAR_URL = "avatar_url"
        private const val ARG_CREATED_AT = "created_at"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_NAME = "name"

        fun newInstance(
            id: String,
            username: String,
            avatarUrl: String?,
            createdAt: Date,
            description: String,
            name: String
        ) = UserInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID, id)
                putString(ARG_USERNAME, username)
                putString(ARG_AVATAR_URL, avatarUrl)
                putSerializable(ARG_CREATED_AT, createdAt)
                putString(ARG_DESCRIPTION, description)
                putString(ARG_NAME, name)
            }
        }
    }
}
