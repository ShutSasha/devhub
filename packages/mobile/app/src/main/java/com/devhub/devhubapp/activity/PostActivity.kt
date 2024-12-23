package com.devhub.devhubapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.PostAPI
import com.devhub.devhubapp.api.ReportAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Comment
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.ReportRequest
import com.devhub.devhubapp.dataClasses.ReportResponse
import com.devhub.devhubapp.dataClasses.SavedPostDetailsResponse
import com.devhub.devhubapp.dataClasses.SavedPostRequest
import com.devhub.devhubapp.dataClasses.SavedPostsResponse
import com.devhub.devhubapp.dataClasses.UserIdRequest
import com.devhub.devhubapp.dataClasses.UserReactions
import com.devhub.devhubapp.fragment.AddCommentFragment
import com.devhub.devhubapp.fragment.CommentFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class PostActivity : AppCompatActivity(), DrawerHandler {
    private lateinit var commentsRecyclerView: RecyclerView
    lateinit var commentCount: TextView
    private lateinit var likeIcon: ImageView
    private lateinit var dislikeIcon: ImageView
    private lateinit var editPostButton: ImageView
    private lateinit var deletePostButton: ImageView
    private lateinit var likeCountTextView: TextView
    private lateinit var dislikeCountTextView: TextView
    private lateinit var saveCountTextView: TextView
    private lateinit var starIcon: ImageView
    private lateinit var userReactions: UserReactions
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var postAPI: PostAPI
    private lateinit var post: Post
    private lateinit var usernameTextView: TextView
    private lateinit var currentUserId: String
    private lateinit var reportPostButton: ImageView
    private lateinit var reportAPI: ReportAPI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var userReports: List<ReportResponse> = emptyList()
    private val REQUEST_CODE_EDIT_POST = 100
    private val postDetailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val updatedPostJson =
                    result.data?.getStringExtra(EditPostActivity.RESULT_UPDATED_POST)
                updatedPostJson?.let {
                    post = Gson().fromJson(it, Post::class.java)
                    displayPost(post)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val displayMetrics = resources.displayMetrics
        navigationView.layoutParams.width = displayMetrics.widthPixels
        navigationView.requestLayout()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.post)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            fragmentTransaction.replace(R.id.header_container_post, HeaderFragment())
            fragmentTransaction.commit()
        }

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        postAPI = RetrofitClient.getInstance(this).postAPI
        currentUserId = encryptedPreferencesManager.getUserData()._id
        val postJson = intent.getStringExtra("post")
        post = Gson().fromJson(postJson, Post::class.java)

        commentsRecyclerView = findViewById(R.id.comments_recycler_view)
        commentCount = findViewById(R.id.comment_count)

        val addCommentFragment = AddCommentFragment().apply {
            arguments = Bundle().apply {
                putString("postId", post._id)
            }
            onCommentAdded = { newComment ->
                updateCommentsList(newComment)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.add_comment_container, addCommentFragment)
            .commit()

        post?.let { displayPost(it) }


        val profileImage: ImageView = findViewById(R.id.profile_image)
        profileImage.setOnClickListener {
            openUserProfileActivity(post.user._id)
        }

        editPostButton = findViewById(R.id.edit_post_button)
        deletePostButton = findViewById(R.id.delete_post_button)
        editPostButton.setOnClickListener {
            val intent = Intent(this, EditPostActivity::class.java).apply {
                putExtra("post", Gson().toJson(post))
            }
            postDetailLauncher.launch(intent)
        }
        deletePostButton.setOnClickListener {
            finish()
        }

        reportAPI = RetrofitClient.getInstance(this).reportAPI

        reportPostButton = findViewById(R.id.report_post_button)
        if (post.user._id == currentUserId) {
            reportPostButton.visibility = View.GONE
        } else {
            reportPostButton.setOnClickListener {
                if (isPostReported()) {
                    Toast.makeText(this, "You have already reported this post", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    showReportDialog()
                }
            }
        }

        userReactions = encryptedPreferencesManager.getUserReactions()
        usernameTextView = findViewById(R.id.username)

        setupPostUI()

        likeIcon = findViewById(R.id.like_icon)
        dislikeIcon = findViewById(R.id.dislike_icon)
        likeCountTextView = findViewById(R.id.like_count)
        dislikeCountTextView = findViewById(R.id.dislike_count)
        saveCountTextView = findViewById(R.id.saved_count)
        starIcon = findViewById(R.id.saved_icon)

        likeCountTextView.text = post.likes.toString()
        dislikeCountTextView.text = post.dislikes.toString()
        saveCountTextView.text = post.saved.toString()

        updateReactionIcons(post._id)
        updateStarIcon(post._id)

        likeIcon.setOnClickListener {
            handleLikeDislike(post._id, true)
        }
        dislikeIcon.setOnClickListener {
            handleLikeDislike(post._id, false)
        }

        starIcon.setOnClickListener {
            toggleSavePost(post._id)
        }

        findViewById<LinearLayout>(R.id.back_button_container).setOnClickListener {
            val intent = Intent()
            intent.putExtra("UPDATE_POSTS", true)
            setResult(RESULT_OK, intent)
            finish()
        }

        fetchUserReports()

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

    private fun displayPost(post: Post) {
        val profileImage: ImageView = findViewById(R.id.profile_image)
        val username: TextView = findViewById(R.id.username)
        val postTitle: TextView = findViewById(R.id.post_title)
        val postImage: ImageView = findViewById(R.id.post_image)
        val postCreateTime: TextView = findViewById(R.id.post_create_time)
        val hashtagsContainer: FlexboxLayout = findViewById(R.id.hashtags_container)
        val postContent: TextView = findViewById(R.id.post_content)
        val likeCount: TextView = findViewById(R.id.like_count)
        val dislikeCount: TextView = findViewById(R.id.dislike_count)
        val commentCount: TextView = findViewById(R.id.comment_count)
        val saveCount: TextView = findViewById(R.id.saved_count)
        val commentsRecyclerView: RecyclerView = findViewById(R.id.comments_recycler_view)

        Glide.with(this)
            .load(post.user.avatar)
            .into(profileImage)
        username.text = post.user.username
        postTitle.text = post.title
        postImage.visibility = if (post.headerImage == "") View.GONE else View.VISIBLE
        Glide.with(this)
            .load("https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/" + post.headerImage)
            .into(postImage)
        postCreateTime.text = formatDate(post.createdAt)
        hashtagsContainer.removeAllViews()
        post.tags?.forEach { tag ->
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 20
            }
            textView.text = "#$tag"
            textView.setTextColor(ContextCompat.getColor(this@PostActivity, R.color.text_primary))
            textView.textSize = 14f
            textView.typeface = ResourcesCompat.getFont(this@PostActivity, R.font.inter_bold_font)
            hashtagsContainer.addView(textView)
        }
        postContent.text = post.content
        likeCount.text = formatCount(post.likes)
        dislikeCount.text = formatCount(post.dislikes)
        saveCount.text = formatCount(post.saved)
        if (post.comments.isNullOrEmpty()) {
            commentCount.text = "0"
        } else {
            commentCount.text = formatCount(post.comments.size)
        }
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        val sortedComments =
            post.comments?.sortedByDescending { it.createdAt }?.toMutableList() ?: mutableListOf()
        val commentAdapter = CommentFragment(sortedComments, currentUserId) {
            decrementCommentCount()
        }
        commentsRecyclerView.adapter = commentAdapter
        commentsRecyclerView.overScrollMode = View.OVER_SCROLL_NEVER
    }

    private fun openUserProfileActivity(userId: String) {
        val intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun decrementCommentCount() {
        val currentCount = commentCount.text.toString().toInt()
        commentCount.text = (currentCount - 1).toString()
    }

    private fun setupPostUI() {
        if (currentUserId == post.user._id) {
            editPostButton.visibility = View.VISIBLE
            deletePostButton.visibility = View.VISIBLE
            editPostButton.setOnClickListener {
                openEditPostActivity()
            }
            deletePostButton.setOnClickListener {
                deletePost()
            }
        } else {
            editPostButton.visibility = View.GONE
            deletePostButton.visibility = View.GONE
        }
    }

    private fun deletePost() {
        postAPI.deletePost(post._id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val intent = Intent()
                    intent.putExtra("UPDATE_POSTS", true)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Log.e(
                        "PostActivity",
                        "Failed to delete post: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("PostActivity", "Error deleting post: ${t.message}", t)
            }
        })
    }

    private fun openEditPostActivity() {
        val intent = Intent(this, EditPostActivity::class.java)
        intent.putExtra("post", Gson().toJson(post))
        postDetailLauncher.launch(intent)
        finish()
    }

    private fun fetchUserReports() {
        reportAPI.getUserReports(currentUserId).enqueue(object : Callback<List<ReportResponse>> {
            override fun onResponse(
                call: Call<List<ReportResponse>>,
                response: Response<List<ReportResponse>>
            ) {
                if (response.isSuccessful) {
                    userReports = response.body() ?: emptyList()
                    if (isPostReported()) {
                        reportPostButton.setImageResource(R.drawable.ic_report_active)
                    }
                } else {
                    Log.e(
                        "PostActivity",
                        "Failed to fetch user reports: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<List<ReportResponse>>, t: Throwable) {
                Log.e("PostActivity", "Error fetching user reports: ${t.message}", t)
            }
        })
    }

    private fun isPostReported(): Boolean {
        return userReports.any { it.content == post._id }
    }

    private fun showReportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_spam).setOnClickListener {
            sendReport("Spam")
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_misinformation).setOnClickListener {
            sendReport("Misinformation")
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_copyright).setOnClickListener {
            sendReport("Copyright Infringement")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendReport(category: String) {
        val reportRequest = ReportRequest(
            sender = currentUserId,
            content = post._id,
            category = category
        )

        reportAPI.sendReport(reportRequest).enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("PostActivity", "Report sent successfully: ${response.body()}")
                    reportPostButton.setImageResource(R.drawable.ic_report_active)
                    Toast.makeText(
                        this@PostActivity,
                        "Post reported successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    userReports = userReports + response.body()!!
                } else {
                    Log.e(
                        "PostActivity",
                        "Failed to send report: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("PostActivity", "Error sending report: ${t.message}", t)
            }
        })
    }


    private fun updateCommentsList(newComment: Comment) {
        val currentComments = (commentsRecyclerView.adapter as CommentFragment).commentsList
        currentComments.add(0, newComment)

        val commentAdapter = CommentFragment(currentComments, currentUserId)
        commentsRecyclerView.adapter = commentAdapter
        commentsRecyclerView.adapter?.notifyDataSetChanged()

        commentCount.text = formatCount(currentComments.size)
    }

    private fun updateReactionIcons(postId: String) {
        val liked = userReactions.likedPosts?.contains(postId) == true
        val disliked = userReactions.dislikedPosts?.contains(postId) == true

        likeIcon.setImageResource(if (liked) R.drawable.ic_like_active else R.drawable.ic_like)
        dislikeIcon.setImageResource(if (disliked) R.drawable.ic_dislike_active else R.drawable.ic_dislike)
    }

    private fun updateStarIcon(postId: String) {
        val savedPostIds = encryptedPreferencesManager.getUserSavedPosts()
        val isSaved = savedPostIds.contains(postId)
        starIcon.setImageResource(if (isSaved) R.drawable.ic_star_active else R.drawable.ic_star)
    }

    private fun handleLikeDislike(postId: String, isLike: Boolean) {
        val userId = encryptedPreferencesManager.getUserData()._id
        val requestBody = UserIdRequest(userId)

        val call = if (isLike) {
            postAPI.likePost(postId, requestBody)
        } else {
            postAPI.dislikePost(postId, requestBody)
        }

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    response.body()?.let { updatedPost ->
                        val updatedLikedPosts =
                            userReactions.likedPosts?.toMutableList() ?: mutableListOf()
                        val updatedDislikedPosts =
                            userReactions.dislikedPosts?.toMutableList() ?: mutableListOf()

                        if (isLike) {
                            if (updatedLikedPosts.contains(postId)) {
                                updatedLikedPosts.remove(postId)
                            } else {
                                updatedLikedPosts.add(postId)
                                updatedDislikedPosts.remove(postId)
                            }
                        } else {
                            if (updatedDislikedPosts.contains(postId)) {
                                updatedDislikedPosts.remove(postId)
                            } else {
                                updatedDislikedPosts.add(postId)
                                updatedLikedPosts.remove(postId)
                            }
                        }

                        userReactions = userReactions.copy(
                            likedPosts = updatedLikedPosts,
                            dislikedPosts = updatedDislikedPosts
                        )
                        encryptedPreferencesManager.saveUserReactions(userReactions)

                        updateReactionIcons(postId)

                        likeCountTextView.text = formatCount(updatedPost.likes)
                        dislikeCountTextView.text = formatCount(updatedPost.dislikes)
                    }
                } else {
                    Log.e(
                        "PostActivity",
                        "Failed to update reaction: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("PostActivity", "Error updating reaction: ${t.message}", t)
            }
        })
    }

    private fun toggleSavePost(postId: String) {
        val userId = encryptedPreferencesManager.getUserData()._id
        val request = SavedPostRequest(userId, postId)

        RetrofitClient.getInstance(this).userAPI.toggleSavePost(request)
            .enqueue(object : Callback<SavedPostDetailsResponse> {
                override fun onResponse(
                    call: Call<SavedPostDetailsResponse>,
                    response: Response<SavedPostDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedPost ->
                            post.saved = updatedPost.saved
                            val savedPostIds =
                                encryptedPreferencesManager.getUserSavedPosts().toMutableList()
                            if (savedPostIds.contains(postId)) {
                                savedPostIds.remove(postId)
                                starIcon.setImageResource(R.drawable.ic_star)
                            } else {
                                savedPostIds.add(postId)
                                starIcon.setImageResource(R.drawable.ic_star_active)
                            }
                            encryptedPreferencesManager.saveUserSavedPosts(savedPostIds)
                            saveCountTextView.text = formatCount(updatedPost.saved)
                        }
                    } else {
                        Log.e(
                            "PostActivity",
                            "Failed to toggle save post: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<SavedPostDetailsResponse>, t: Throwable) {
                    Log.e("PostActivity", "Error toggling save post: ${t.message}", t)
                }
            })
    }

    private fun refreshPost() {
        postAPI.getPostById(post._id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    response.body()?.let { updatedPost ->
                        post = updatedPost
                        displayPost(post)
                    }
                } else {
                    Log.e(
                        "PostActivity",
                        "Failed to refresh post: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("PostActivity", "Failed to refresh post: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshPost()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_POST && resultCode == RESULT_OK) {
            val updatedPostJson = data?.getStringExtra(EditPostActivity.RESULT_UPDATED_POST)
            updatedPostJson?.let {
                post = Gson().fromJson(it, Post::class.java)
                displayPost(post)
            }
        }
    }

    private fun formatCount(reaction: Int?): String {
        return when {
            reaction == null || reaction == 0 -> "0"
            reaction >= 1000 -> String.format("%.1fK", reaction / 1000.0)
            else -> reaction.toString()
        }
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }
}