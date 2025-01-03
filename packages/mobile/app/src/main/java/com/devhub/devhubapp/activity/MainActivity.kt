package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.UserReactions
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.PostFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), DrawerHandler {
    @SuppressLint("CommitTransaction")
    private var currentPage = 1
    private var isLoading = false
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var userReactions: UserReactions
    private val existingPostsIds = mutableSetOf<String>()
    private val REQUEST_CODE_CREATE_POST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val displayMetrics = resources.displayMetrics
        navigationView.layoutParams.width = displayMetrics.widthPixels
        navigationView.requestLayout()

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        val user = encryptedPreferencesManager.getUserData()
        val retrofitClient = RetrofitClient.getInstance(this)
        val userAPI = retrofitClient.userAPI

        encryptedPreferencesManager.fetchAndSaveUserSavedPosts(userAPI)

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

        if (savedInstanceState == null) {
            val footerFragment = FooterFragment.newInstance("home")
            supportFragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, footerFragment)
                .commit()
        }

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
            intent.removeExtra("UPDATE_POSTS")
        }

        setupDrawer()
    }

    private fun setupDrawer() {
        val headerView = navigationView.getHeaderView(0)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.nav_user_avatar)
        val closeImageView = headerView.findViewById<ImageView>(R.id.nav_close)

        val user = encryptedPreferencesManager.getUserData()
        if (user.avatar.isNotEmpty()) {
            Glide.with(this)
                .load(user.avatar)
                .into(avatarImageView)
        }

        closeImageView.setOnClickListener {
            drawerLayout.closeDrawers()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_my_posts -> {
                    // Handle My Posts action
                    true
                }

                R.id.nav_notifications -> {
                    val intent = Intent(this, NotificationsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_logout -> {
                    encryptedPreferencesManager.deleteUserData()
                    finish()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun openDrawer() {
        drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }

    private fun fetchUserReactions() {
        GlobalScope.launch(Dispatchers.Main) {
            userReactions = fetchUserReactionsInternal() ?: UserReactions(emptyList(), emptyList())
        }
    }

    private suspend fun fetchUserReactionsInternal(): UserReactions? {
        return try {
            withContext(Dispatchers.IO) {
                val userId = encryptedPreferencesManager.getUserData()._id
                val response =
                    RetrofitClient.getInstance(applicationContext).userAPI.getUserReactions(userId)
                        .execute()
                if (response.isSuccessful) response.body() else null
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching user reactions: ${e.message}", e)
            null
        }
    }

    override fun onResume() {
        super.onResume()
        currentPage = 1
        fetchPostsAndDisplay(currentPage)
        refreshPosts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_POST && resultCode == RESULT_OK) {
            val updatePosts = data?.getBooleanExtra("UPDATE_POSTS", false) ?: false
            if (updatePosts) {
                refreshPosts()
            }
        }
    }

    fun refreshPosts() {
        currentPage = 1
        isLoading = false
        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is PostFragment) {
                fragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        existingPostsIds.clear()

        findViewById<LinearLayout>(R.id.posts_container).removeAllViews()

        fetchPostsAndDisplay(currentPage)
    }

    private fun fetchPostsAndDisplay(page: Int) {
        if (page == 1) {
            findViewById<LinearLayout>(R.id.posts_container).removeAllViews()
        }
        isLoading = true
        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (!this@MainActivity::userReactions.isInitialized) {
                    userReactions =
                        fetchUserReactionsInternal() ?: UserReactions(emptyList(), emptyList())
                }

                val postResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.getInstance(applicationContext).postAPI.getPosts(
                        limit = 10,
                        page = page
                    )
                }
                val posts = postResponse

                if (posts.isNotEmpty()) {
                    displayPosts(posts, userReactions)
                }
                isLoading = false
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching posts: ${e.message}", e)
                isLoading = false
            }
        }
    }

    private fun displayPosts(posts: List<Post>, reactions: UserReactions?) {
        val fragmentManager = supportFragmentManager
        val container = findViewById<LinearLayout>(R.id.posts_container)

        for (post in posts) {
            if (existingPostsIds.contains(post._id)) continue
            val postFragment = PostFragment.newInstance(post, reactions)
            fragmentManager.beginTransaction()
                .add(container.id, postFragment)
                .commit()
            existingPostsIds.add(post._id)
        }
    }
}