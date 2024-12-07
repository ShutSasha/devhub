package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Follower
import com.devhub.devhubapp.databinding.ActivityFollowersBinding
import com.devhub.devhubapp.fragment.FollowerFragment
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.UnderlinedTextFragment
import com.devhub.devhubapp.interfaces.OnFollowStateChangedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersActivity : AppCompatActivity(), OnFollowStateChangedListener {
    private lateinit var binding: ActivityFollowersBinding
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var followings: List<Follower>
    private lateinit var followers: List<Follower>
    private var userId: String = ""
    private var username: String = ""

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_followers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.followersContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val footerFragment = FooterFragment.newInstance("followers")
            supportFragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, footerFragment)
                .commit()
        }

        binding = ActivityFollowersBinding.inflate(layoutInflater)
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: ""
        username = intent.getStringExtra("USERNAME") ?: ""

        setUpFragments()
    }

    private fun setUpFragments(){
        binding.noFollowingsContainer.visibility = View.GONE
        binding.noFollowersContainer.visibility = View.GONE

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val userName = encryptedPreferencesManager.getData("username")
        val targetUsername = if (username.isNotEmpty() && username != userName) username else userName
        binding.tvUsername.text = "@${targetUsername}"

        val followersFragment = UnderlinedTextFragment()
        val followedFragment = UnderlinedTextFragment()

        followersFragment.apply {
            setText("Followers")
            isUnderlined(true)
            setAction {
                this.isUnderlined(true)
                followedFragment.isUnderlined(false)
                binding.followers.visibility = View.VISIBLE
                binding.followings.visibility = View.GONE
                binding.noFollowingsContainer.visibility = View.GONE
                if(followers.isEmpty()){
                    binding.noFollowersContainer.visibility = View.VISIBLE
                } else {
                    binding.noFollowersContainer.visibility = View.GONE
                }
            }
        }
        fragmentTransaction.add(R.id.tvFollowers, followersFragment)

        followedFragment.apply {
            setText("Followed")
            isUnderlined(false)
            setAction {
                this.isUnderlined(true)
                followersFragment.isUnderlined(false)
                binding.followers.visibility = View.GONE
                binding.followings.visibility = View.VISIBLE
                binding.noFollowersContainer.visibility = View.GONE
                if(followings.isEmpty()){
                    binding.noFollowingsContainer.visibility = View.VISIBLE
                } else {
                    binding.noFollowingsContainer.visibility = View.GONE
                }
            }
        }
        fragmentTransaction.add(R.id.tvFollowed, followedFragment)

        if(followersFragment.isUnderlined){
            binding.followers.visibility = View.VISIBLE
            binding.followings.visibility = View.GONE
        } else {
            binding.followers.visibility = View.GONE
            binding.followings.visibility = View.VISIBLE
        }

        fragmentTransaction.commit()
    }

    private fun getFollowings(id: String) {
        val userId = encryptedPreferencesManager.getData("user_id")
        val targetId = if (id.isNotEmpty() && id != userId) id else userId

        RetrofitClient.getInstance(applicationContext).userAPI.getFollowings(targetId)
            .enqueue(object : Callback<List<Follower>> {
                override fun onResponse(call: Call<List<Follower>>, response: Response<List<Follower>>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            followings = data
                            displayFollowings(followings)
                            Log.i("GetFollowings", "Successfully retrieved: $followings")
                        } else {
                            Log.e("GetFollowings", "Response body is null")
                        }
                    } else {
                        Log.e("GetFollowings", "Failed with error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<Follower>>, t: Throwable) {
                    Log.e("GetFollowings", "Request failed: ${t.message}")
                }
            })
    }

    private fun getFollowers(id: String) {
        val userId = encryptedPreferencesManager.getData("user_id")
        val targetId = if (id.isNotEmpty() && id != userId) id else userId

        RetrofitClient.getInstance(applicationContext).userAPI.getFollowers(targetId)
            .enqueue(object : Callback<List<Follower>> {
                override fun onResponse(call: Call<List<Follower>>, response: Response<List<Follower>>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            followers = data
                            displayFollowers(followers)
                            Log.i("GetFollowers", "Successfully retrieved: $followers")
                        } else {
                            Log.e("GetFollowers", "Response body is null")
                        }
                    } else {
                        Log.e("GetFollowers", "Failed with error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<Follower>>, t: Throwable) {
                    Log.e("GetFollowers", "Request failed: ${t.message}")
                }
            })
    }

    private fun displayFollowings(followings: List<Follower>) {
        val container = findViewById<LinearLayout>(R.id.followings_container)
        container.removeAllViews()

        val fragmentManager = supportFragmentManager

        for (follower in followings) {
            val tag = "follower_${follower._id}"
            val fragment = FollowerFragment.newInstance(
                username = follower.username,
                avatarUrl = follower.avatar,
                userId = if (userId.isNotEmpty() && userId != encryptedPreferencesManager.getData("user_id")) userId else encryptedPreferencesManager.getData("user_id"),
                targetUserId = follower._id
            )
            isFollowing(userId, follower._id) { isFollowing ->
                fragment.setFollowState(isFollowing)
                fragmentManager.beginTransaction()
                    .add(container.id, fragment, tag)
                    .commit()
            }
        }
    }

    private fun displayFollowers(followers: List<Follower>) {
        val container = findViewById<LinearLayout>(R.id.followers_container)
        container.removeAllViews()

        val fragmentManager = supportFragmentManager

        for (follower in followers) {
            val tag = "follower_${follower._id}"
            val fragment = FollowerFragment.newInstance(
                username = follower.username,
                avatarUrl = follower.avatar,
                userId = if (userId.isNotEmpty() && userId != encryptedPreferencesManager.getData("user_id")) userId else encryptedPreferencesManager.getData("user_id"),
                targetUserId = follower._id
            )

            isFollowing(userId, follower._id) { isFollowing ->
                fragment.setFollowState(isFollowing)
                fragmentManager.beginTransaction()
                    .add(container.id, fragment, tag)
                    .commit()
            }
        }
    }

    private fun isFollowing(id: String, followerId: String, callback: (Boolean) -> Unit) {
        val userId = encryptedPreferencesManager.getData("user_id")

        RetrofitClient.getInstance(applicationContext).userAPI.isFollowing(userId, followerId)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        val isFollowingResponse = response.body()
                        if (isFollowingResponse != null) {
                            callback(isFollowingResponse)
                            Log.i("IsFollowing", "IsFollowing successfully retrieved: $isFollowingResponse")
                        } else {
                            Log.e("IsFollowing", "Response body is null")
                        }
                    } else {
                        Log.e("IsFollowing", "Failed with error: ${response.errorBody()?.string()}")
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("IsFollowing", "Request failed: ${t.message}")
                    callback(false)
                }
            })
    }

    override fun onFollowStateChanged() {
        super.onResume()
        getFollowings(userId)
        getFollowers(userId)
    }

    override fun onResume() {
        super.onResume()

        getFollowings(userId)
        getFollowers(userId)
    }

}