package com.devhub.devhubapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
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
import com.devhub.devhubapp.activity.EditUserProfileActivity
import com.devhub.devhubapp.activity.FollowersActivity
import com.devhub.devhubapp.activity.UserProfileActivity
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.FollowRequest
import com.devhub.devhubapp.dataClasses.UserDetail
import com.devhub.devhubapp.databinding.FragmentUserInfoBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private var isFollowing: Boolean = false

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
            val followers = it.getStringArray(ARG_FOLLOWERS) ?: emptyArray()
            val createdAt = it.getSerializable(ARG_CREATED_AT) as? Date ?: Date()
            val description = it.getString(ARG_DESCRIPTION) ?: ""
            val name = it.getString(ARG_NAME) ?: ""

            if (encryptedPreferencesManager.getData("user_id") != id) {
                isFollowing(id)
            }

            setUpFragment(id, username, avatarUrl, followers, createdAt, description, name)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpFragment(
        id: String,
        username: String,
        avatarUrl: String,
        followers: Array<String>?,
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

        if (followers != null) {
            binding.tvFollowers.text = "${followers.size} followers"
        }
        binding.tvFollowers.setOnClickListener {
            val intent = Intent(requireContext(), FollowersActivity::class.java)
            intent.putExtra("USER_ID", id)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

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

            if(isFollowing){
                binding.btnFollowPrimary.visibility = View.GONE
                binding.btnContainer.visibility = View.VISIBLE

                val primaryUnfollowButtonFragment = OutlinedButtonFragment()
                primaryUnfollowButtonFragment.setButtonText("Unfollow")
                primaryUnfollowButtonFragment.setButtonAction {
                    unfollow(id)
                    binding.btnFollowPrimary.visibility = View.VISIBLE
                    binding.btnContainer.visibility = View.GONE
                }
                fragmentTransaction.add(R.id.btnUnfollowOutlined, primaryUnfollowButtonFragment)

                val outlinedSendMessageButtonFragment = OutlinedButtonFragment()
                outlinedSendMessageButtonFragment.setButtonText("Send message")
                outlinedSendMessageButtonFragment.setButtonAction {
                    // Button action
                }
                fragmentTransaction.add(R.id.btnSendMessageOutlined, outlinedSendMessageButtonFragment)

            } else {
                binding.btnFollowPrimary.visibility = View.VISIBLE
                binding.btnContainer.visibility = View.GONE

                val primaryFollowButtonFragment = PrimaryButtonFragment()
                primaryFollowButtonFragment.setButtonText("Follow")
                primaryFollowButtonFragment.setButtonAction {
                    follow(id)
                    binding.btnFollowPrimary.visibility = View.GONE
                    binding.btnContainer.visibility = View.VISIBLE
                }
                fragmentTransaction.add(R.id.btnFollowPrimary, primaryFollowButtonFragment)

            }

        } else{
            binding.btnFollowPrimary.visibility = View.GONE
            binding.btnContainer.visibility = View.GONE

            val editOutlinedButtonFragment = OutlinedButtonFragment()
            editOutlinedButtonFragment.setButtonText("Edit profile")
            editOutlinedButtonFragment.setButtonAction {
                val intent = Intent(requireContext(), EditUserProfileActivity::class.java)
                intent.putExtra("DESCRIPTION", description)
                startActivity(intent)
            }
            fragmentTransaction.add(R.id.editBtnOutlined, editOutlinedButtonFragment)
        }

        fragmentTransaction.commit()
    }

    private fun isFollowing(id: String) {
        RetrofitClient.getInstance(requireContext()).userAPI.isFollowing(encryptedPreferencesManager.getData("user_id"), id)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        val isFollowingResponse = response.body()
                        if (isFollowingResponse != null) {
                            isFollowing = isFollowingResponse
                            Log.i("IsFollowing", "IsFollowing successfully retrieved: $isFollowing")
                            arguments?.let {
                                setUpFragment(
                                    id,
                                    it.getString(ARG_USERNAME) ?: "",
                                    it.getString(ARG_AVATAR_URL) ?: "",
                                    it.getStringArray(ARG_FOLLOWERS) ?: emptyArray(),
                                    it.getSerializable(ARG_CREATED_AT) as? Date ?: Date(),
                                    it.getString(ARG_DESCRIPTION) ?: "",
                                    it.getString(ARG_NAME) ?: ""
                                )
                            }
                        } else {
                            Log.e("IsFollowing", "Response body is null")
                        }
                    } else {
                        Log.e("IsFollowing", "Failed with error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("IsFollowing", "Request failed: ${t.message}")
                }
            })
    }

    private fun follow(id: String) {
        val usersId = FollowRequest(
            encryptedPreferencesManager.getData("user_id"),
            id
        )

        RetrofitClient.getInstance(requireContext()).userAPI.follow(usersId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Log.i("Following", "Following successfully retrieved")
                            isFollowing = true
                            arguments?.let {
                                setUpFragment(
                                    id,
                                    it.getString(ARG_USERNAME) ?: "",
                                    it.getString(ARG_AVATAR_URL) ?: "",
                                    it.getStringArray(ARG_FOLLOWERS) ?: emptyArray(),
                                    it.getSerializable(ARG_CREATED_AT) as? Date ?: Date(),
                                    it.getString(ARG_DESCRIPTION) ?: "",
                                    it.getString(ARG_NAME) ?: ""
                                )
                            }
                        } else {
                            Log.e("Following", "Response body is null")
                        }
                    } else {
                        Log.e("Following", "Failed with error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Following", "Request failed: ${t.message}")
                }
            })
    }

    private fun unfollow(id: String) {

        RetrofitClient.getInstance(requireContext()).userAPI.unfollow(encryptedPreferencesManager.getData("user_id"), id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Log.i("Unfollowing", "Unfollowing successfully retrieved")
                            isFollowing = true
                        } else {
                            Log.e("Unfollowing", "Response body is null")
                        }
                    } else {
                        Log.e("Unfollowing", "Failed with error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Unfollowing", "Request failed: ${t.message}")
                }
            })
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_USERNAME = "username"
        private const val ARG_AVATAR_URL = "avatar_url"
        private const val ARG_FOLLOWERS = "followers"
        private const val ARG_CREATED_AT = "created_at"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_NAME = "name"

        fun newInstance(
            id: String,
            username: String,
            avatarUrl: String?,
            followers: Array<String>?,
            createdAt: Date,
            description: String,
            name: String
        ) = UserInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID, id)
                putString(ARG_USERNAME, username)
                putString(ARG_AVATAR_URL, avatarUrl)
                putStringArray(ARG_FOLLOWERS, followers)
                putSerializable(ARG_CREATED_AT, createdAt)
                putString(ARG_DESCRIPTION, description)
                putString(ARG_NAME, name)
            }
        }
    }
}
