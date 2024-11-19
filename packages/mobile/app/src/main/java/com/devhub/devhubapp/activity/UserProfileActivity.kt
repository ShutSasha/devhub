package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.dataClasses.UserDetail
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.IconAndTextFragment
import com.devhub.devhubapp.fragment.PostFragment
import com.devhub.devhubapp.fragment.UserInfoFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UserProfileActivity : AppCompatActivity() {
    private lateinit var user: UserDetail
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        user = UserDetail(
            _id = "",
            name = "",
            username = "",
            avatar = "",
            createdAt = Date(),
            bio = "",
            posts = emptyList(),
            comments = emptyList()
        )

        userId = intent.getStringExtra("USER_ID") ?: ""

        if (userId.isNotEmpty()) {
            getUserData(userId)
        } else {
            Log.e("UserProfile", "User ID is missing")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userProfileContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, FooterFragment())
                .commit()
        }

    }

    private fun setUpFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val userInfoFragment = UserInfoFragment.newInstance(
            id = user._id,
            username = user.username,
            avatarUrl = user.avatar,
            createdAt = user.createdAt,
            description = user.bio ?: "",
            name = user.name ?: ""
        )
        fragmentTransaction.add(R.id.userInfoContainer, userInfoFragment)

        val postsText = if ((user.posts?.size ?: 0) == 1) {
            "${user.posts?.size} post published"
        } else {
            "${user.posts?.size} posts published"
        }
        val postsNumberFragment = IconAndTextFragment.newInstance(postsText, R.drawable.ic_post)
        fragmentTransaction.add(R.id.postsNumberContainer, postsNumberFragment)

        val commentsText = if ((user.comments?.size ?: 0) == 1) {
            "${user.comments?.size} comment written"
        } else {
            "${user.comments?.size} comments written"
        }
        val commentsNumberFragment = IconAndTextFragment.newInstance(commentsText, R.drawable.ic_comment)
        fragmentTransaction.add(R.id.commentsNumberContainer, commentsNumberFragment)

        val container = findViewById<LinearLayout>(R.id.posts_container)
        val posts = user.posts ?: emptyList()

        for (post in posts) {
            post.user = User(
                _id = user._id,
                name = user.name,
                username = user.username,
                avatar = user.avatar,
                email = "",
                createdAt = Date(),
                devPoints = 0,
                activationCode = "",
                isActivated = true,
                roles = emptyArray()
            )
            val postFragment = PostFragment.newInstance(post)
            fragmentManager.beginTransaction()
                .add(container.id, postFragment)
                .commit()
        }

        fragmentTransaction.commit()
    }

    private fun getUserData(id: String) {
        RetrofitClient.getInstance(applicationContext).userAPI.getUserDetail(id).enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        user = data
                        Log.i("UserDetail", "UserDetail successfully retrieved")
                        Log.e("UserDetail", user.posts.toString())
                        setUpFragments()
                    } else {
                        Log.e("UserDetail", "Response body is null")
                    }
                } else {
                    Log.e("UserDetail", "Failed with error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                Log.e("UserDetail", "Request failed: ${t.message}")
            }
        })
    }

    private fun String.toDate(): Date {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.parse(this) ?: Date()
    }
}

