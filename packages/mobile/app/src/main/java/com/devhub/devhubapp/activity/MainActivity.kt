package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.PostFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val userAvatar = intent.getStringExtra("USER_AVATAR")

        if (savedInstanceState == null) {
            val headerFragment = HeaderFragment()

            val args = Bundle()
            args.putString("USER_AVATAR", userAvatar)
            headerFragment.arguments = args

            fragmentTransaction.replace(R.id.header_container, headerFragment)
            fragmentTransaction.replace(R.id.footer_container, FooterFragment())
            fragmentTransaction.commit()
        }

        fetchPostsAndDisplay()
    }

    private fun fetchPostsAndDisplay() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val posts = withContext(Dispatchers.IO) {
                    RetrofitClient.getInstance(applicationContext).postAPI.getPosts(limit = 10, page = 1)
                }
                if (posts.isNotEmpty()) {
                    displayPosts(posts)
                } else {
                    Log.d("MainActivity", "No posts received.")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching posts: ${e.message}", e)
            }
        }
    }

    private fun displayPosts(posts: List<Post>) {
        val fragmentManager = supportFragmentManager
        val container = findViewById<LinearLayout>(R.id.posts_container)

        for (post in posts) {
            val postFragment = PostFragment.newInstance(post)
            fragmentManager.beginTransaction()
                .add(container.id, postFragment)
                .commit()
        }
    }
}
