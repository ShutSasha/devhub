package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
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
    private var currentPage = 1
    private var isLoading = false
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        val user = encryptedPreferencesManager.getUserData()

        if (user._id.isEmpty()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        fetchPostsAndDisplay(currentPage)

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))

            if (diff <= 0 && !isLoading) {
                currentPage++
                fetchPostsAndDisplay(currentPage)
            }
        }

        if (intent.getBooleanExtra("UPDATE_POSTS", false)) {
            refreshPosts()
        }
    }

    private fun refreshPosts() {
        currentPage = 1
        findViewById<LinearLayout>(R.id.posts_container).removeAllViews()
        fetchPostsAndDisplay(currentPage)
    }

    private fun fetchPostsAndDisplay(page: Int) {
        isLoading = true
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val posts = withContext(Dispatchers.IO) {
                    RetrofitClient.getInstance(applicationContext).postAPI.getPosts(limit = 10, page = page)
                }
                if (posts.isNotEmpty()) {
                    displayPosts(posts)
                }
                isLoading = false
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching posts: ${e.message}", e)
                isLoading = false
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
