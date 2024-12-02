package com.devhub.devhubapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.RegistrationActivity
import com.devhub.devhubapp.activity.UserProfileActivity
import com.devhub.devhubapp.databinding.FragmentFollowerBinding
import com.devhub.devhubapp.databinding.FragmentUnderlinedTextBinding

class FollowerFragment : Fragment() {
    private lateinit var binding: FragmentFollowerBinding
    private var isFollowed: Boolean = false

    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_AVATAR_URL = "avatarUrl"
        private const val ARG_USER_ID = "userId"

        fun newInstance(username: String, avatarUrl: String, userId: String): FollowerFragment {
            val fragment = FollowerFragment()
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putString(ARG_AVATAR_URL, avatarUrl)
                putString(ARG_USER_ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("CommitTransaction")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowerBinding.inflate(inflater, container, false)

        val username = arguments?.getString(ARG_USERNAME)
        val avatarUrl = arguments?.getString(ARG_AVATAR_URL)
        val userId = arguments?.getString(ARG_USER_ID)

        binding.tvUsername.text = username
        avatarUrl?.let { setAvatar(it) }

        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Follow")
        primaryButtonFragment.setButtonAction {

        }
        fragmentTransaction.add(R.id.primaryButton, primaryButtonFragment)

        val outlinedButtonFragment = OutlinedButtonFragment()
        outlinedButtonFragment.setButtonText("Unfollow")
        outlinedButtonFragment.setButtonAction {

        }
        fragmentTransaction.add(R.id.outlinedButton, outlinedButtonFragment)

        if (isFollowed) {
            binding.primaryButton.visibility = View.GONE
        } else {
            binding.outlinedButton.visibility = View.GONE
        }

        binding.ivAvatar.setOnClickListener{
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.tvUsername.setOnClickListener{
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        fragmentTransaction.commit()
        return binding.root
    }

    fun isFollowed(followed: Boolean) {
        isFollowed = followed
    }

    private fun setAvatar(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivAvatar)
    }
}
