package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.devhub.devhubapp.dataClasses.Comment
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.SavedPostDetailsResponse
import com.devhub.devhubapp.dataClasses.UserReactions
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.PostFragment
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedPostsActivity : AppCompatActivity(), DrawerHandler {
    @SuppressLint("CommitTransaction")
    private var currentPage = 1
    private var isLoading = false
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var userReactions: UserReactions
    private val displayedPostIds = mutableSetOf<String>()
    private val REQUEST_CODE_CREATE_POST = 101
    private lateinit var emptyStateImage: ImageView
    private lateinit var scrollView: ScrollView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_saved_posts)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val displayMetrics = resources.displayMetrics
        navigationView.layoutParams.width = displayMetrics.widthPixels
        navigationView.requestLayout()

        emptyStateImage = findViewById(R.id.empty_state_image)
        scrollView = findViewById(R.id.scrollView)
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        val user = encryptedPreferencesManager.getUserData()

        if (user._id.isEmpty()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.saved_posts)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val footerFragment = FooterFragment.newInstance("saved")
            supportFragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, footerFragment)
                .commit()
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))

            if (diff <= 0 && !isLoading) {
                currentPage++
                loadSavedPosts(user._id)
            }
        }

        if (intent.getBooleanExtra("UPDATE_POSTS", false)) {
            refreshPosts()
            intent.removeExtra("UPDATE_POSTS")
        }

        fetchUserReactions(user._id)

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

    private fun loadSavedPosts(userId: String) {
        val retrofitClient = RetrofitClient.getInstance(this)
        val userAPI = retrofitClient.userAPI

        isLoading = true
        userAPI.getSavedPosts(userId).enqueue(object : Callback<List<SavedPostDetailsResponse>> {
            override fun onResponse(
                call: Call<List<SavedPostDetailsResponse>>,
                response: Response<List<SavedPostDetailsResponse>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val savedPosts = response.body()

                    if (!savedPosts.isNullOrEmpty()) {
                        emptyStateImage.visibility = View.GONE
                        scrollView.visibility = View.VISIBLE
                        val newPosts = savedPosts.filter { !displayedPostIds.contains(it._id) }
                        val posts = newPosts.map { savedPost ->
                            displayedPostIds.add(savedPost._id)
                            Post(
                                _id = savedPost._id,
                                headerImage = savedPost.headerImage,
                                user = savedPost.user,
                                title = savedPost.title,
                                content = savedPost.content,
                                createdAt = savedPost.createdAt,
                                likes = savedPost.likes,
                                dislikes = savedPost.dislikes,
                                saved = savedPost.saved,
                                comments = savedPost.comments?.map { commentId ->
                                    Comment(
                                        commentId,
                                        "",
                                        "",
                                        0,
                                        0,
                                        savedPost.user,
                                        savedPost._id
                                    )
                                },
                                tags = savedPost.tags
                            )
                        }
                        displayPosts(posts)
                    } else {
                        emptyStateImage.visibility = View.VISIBLE
                        scrollView.visibility = View.GONE
                        Log.e("SavedPostsActivity", "No saved posts found")
                    }
                } else {
                    Log.e(
                        "SavedPostsActivity",
                        "Failed to fetch saved posts: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<List<SavedPostDetailsResponse>>, t: Throwable) {
                isLoading = false
                Log.e("SavedPostsActivity", "Error fetching saved posts: ${t.message}", t)
            }
        })
    }

    private fun fetchUserReactions(userId: String) {
        val retrofitClient = RetrofitClient.getInstance(this)
        val userAPI = retrofitClient.userAPI

        userAPI.getUserReactions(userId).enqueue(object : Callback<UserReactions> {
            override fun onResponse(call: Call<UserReactions>, response: Response<UserReactions>) {
                if (response.isSuccessful) {
                    userReactions = response.body() ?: UserReactions()
                    loadSavedPosts(userId)
                } else {
                    Log.e(
                        "SavedPostsActivity",
                        "Failed to fetch user reactions: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<UserReactions>, t: Throwable) {
                Log.e("SavedPostsActivity", "Error fetching user reactions: ${t.message}", t)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        currentPage = 1
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
        displayedPostIds.clear()

        findViewById<LinearLayout>(R.id.posts_container).removeAllViews()

        loadSavedPosts(encryptedPreferencesManager.getUserData()._id)
    }

    private fun displayPosts(posts: List<Post>) {
        val postsContainer = findViewById<LinearLayout>(R.id.posts_container)
        posts.forEach { post ->
            val postFragment = PostFragment.newInstance(post, userReactions)
            supportFragmentManager.beginTransaction()
                .add(postsContainer.id, postFragment)
                .commit()
        }
    }
}