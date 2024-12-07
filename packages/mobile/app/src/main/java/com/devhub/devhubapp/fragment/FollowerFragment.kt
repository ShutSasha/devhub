package com.devhub.devhubapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.FollowRequest
import com.devhub.devhubapp.databinding.FragmentFollowerBinding
import com.devhub.devhubapp.databinding.FragmentUnderlinedTextBinding
import com.devhub.devhubapp.interfaces.OnFollowStateChangedListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowerFragment : Fragment() {
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var binding: FragmentFollowerBinding
    private var isFollowed: MutableLiveData<Boolean> = MutableLiveData(false)
    private var userId: String? = ""
    private var targetUserId: String = ""
    private lateinit var listener: OnFollowStateChangedListener

    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_AVATAR_URL = "avatarUrl"
        private const val ARG_USER_ID = "userId"
        private const val ARG_FOLLOWING_USER_ID = "followingUserId"

        fun newInstance(username: String, avatarUrl: String, userId: String?, targetUserId: String): FollowerFragment {
            val fragment = FollowerFragment()
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putString(ARG_AVATAR_URL, avatarUrl)
                putString(ARG_USER_ID, userId)
                putString(ARG_FOLLOWING_USER_ID, targetUserId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        binding = FragmentFollowerBinding.inflate(inflater, container, false)

        val username = arguments?.getString(ARG_USERNAME)
        val avatarUrl = arguments?.getString(ARG_AVATAR_URL)
        userId = arguments?.getString(ARG_USER_ID) ?: ""
        targetUserId = arguments?.getString(ARG_FOLLOWING_USER_ID) ?: ""

        binding.tvUsername.text = username
        avatarUrl?.let { setAvatar(it) }

        binding.ivAvatar.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("USER_ID", targetUserId)
            startActivity(intent)
        }

        binding.tvUsername.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("USER_ID", targetUserId)
            startActivity(intent)
        }

        if(targetUserId != encryptedPreferencesManager.getData("user_id")){
            isFollowed.observe(viewLifecycleOwner) { followed ->
                if (followed) {
                    binding.primaryButton.visibility = View.GONE
                    binding.outlinedButton.visibility = View.VISIBLE
                } else {
                    binding.primaryButton.visibility = View.VISIBLE
                    binding.outlinedButton.visibility = View.GONE
                }
            }
        } else {
            binding.primaryButton.visibility = View.GONE
            binding.outlinedButton.visibility = View.GONE
        }

        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Follow")
        primaryButtonFragment.setButtonAction {
            if (!isFollowed.value!!) {
                follow(targetUserId)
            }
        }
        fragmentTransaction.add(R.id.primaryButton, primaryButtonFragment)

        val outlinedButtonFragment = OutlinedButtonFragment()
        outlinedButtonFragment.setButtonText("Messages")
        outlinedButtonFragment.setButtonAction {
            // Message button action (currently empty)
        }
        fragmentTransaction.add(R.id.outlinedButton, outlinedButtonFragment)

        fragmentTransaction.commit()
        return binding.root
    }

    fun setFollowState(followed: Boolean) {
        isFollowed.value = followed
    }

    private fun setAvatar(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivAvatar)
    }

    private fun follow(followingUserId: String) {
        val usersId = FollowRequest(encryptedPreferencesManager.getData("user_id"), followingUserId)

        RetrofitClient.getInstance(requireContext()).userAPI.follow(usersId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        isFollowed.value = true
                        listener.onFollowStateChanged()
                    } else {
                        Log.e("Following", "Failed with error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Following", "Request failed: ${t.message}")
                }
            })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFollowStateChangedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFollowStateChangedListener")
        }
    }
}

