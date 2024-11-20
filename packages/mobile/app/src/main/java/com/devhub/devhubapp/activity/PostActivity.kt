package com.devhub.devhubapp.activity

import com.devhub.devhubapp.fragment.CommentFragment
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.fragment.HeaderFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhub.devhubapp.dataClasses.Comment
import com.devhub.devhubapp.fragment.AddCommentFragment
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.devhub.devhubapp.api.PostAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.UserIdRequest
import com.devhub.devhubapp.dataClasses.UserReactions
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity() {
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentCount: TextView
    private lateinit var likeIcon: ImageView
    private lateinit var dislikeIcon: ImageView
    private lateinit var editPostButton: ImageView
    private lateinit var likeCountTextView: TextView
    private lateinit var dislikeCountTextView: TextView
    private lateinit var userReactions: UserReactions
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var postAPI: PostAPI
    private lateinit var post: Post
    private lateinit var usernameTextView: TextView
    private lateinit var currentUserId: String
    private val REQUEST_CODE_EDIT_POST = 100
    private val postDetailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val updatedPostJson = result.data?.getStringExtra(EditPostActivity.RESULT_UPDATED_POST)
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.post)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editPostButton = findViewById(R.id.edit_post_button)
        editPostButton.setOnClickListener {
            val intent = Intent(this, EditPostActivity::class.java).apply {
                putExtra("post", Gson().toJson(post))
            }
            postDetailLauncher.launch(intent)
        }

        userReactions = encryptedPreferencesManager.getUserReactions()
        usernameTextView = findViewById(R.id.username)

        setupPostUI()

        likeIcon = findViewById(R.id.like_icon)
        dislikeIcon = findViewById(R.id.dislike_icon)
        likeCountTextView = findViewById(R.id.like_count)
        dislikeCountTextView = findViewById(R.id.dislike_count)

        likeCountTextView.text = post.likes.toString()
        dislikeCountTextView.text = post.dislikes.toString()

        updateReactionIcons(post._id)

        likeIcon.setOnClickListener {
            handleLikeDislike(post._id, true)
        }
        dislikeIcon.setOnClickListener {
            handleLikeDislike(post._id, false)
        }

        findViewById<LinearLayout>(R.id.back_button_container).setOnClickListener {
            val intent = Intent()
            intent.putExtra("UPDATE_POSTS", true)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    private fun displayPost(post: Post) {
        val profileImage: ImageView = findViewById(R.id.profile_image)
        val username: TextView = findViewById(R.id.username)
        val postTitle: TextView = findViewById(R.id.post_title)
        val postImage: ImageView = findViewById(R.id.post_image)
        val postCreateTime: TextView = findViewById(R.id.post_create_time)
        val postContent: TextView = findViewById(R.id.post_content)
        val likeCount: TextView = findViewById(R.id.like_count)
        val dislikeCount: TextView = findViewById(R.id.dislike_count)
        commentCount = findViewById(R.id.comment_count)
        commentsRecyclerView = findViewById(R.id.comments_recycler_view)
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        val sortedComments = post.comments!!.sortedByDescending { it.createdAt }
        val commentAdapter = CommentFragment(sortedComments)
        commentsRecyclerView.adapter = commentAdapter
        commentsRecyclerView.overScrollMode = View.OVER_SCROLL_NEVER

        postImage.visibility = if (post.headerImage == "") View.GONE else View.VISIBLE

        Glide.with(this)
            .load(post.user.avatar)
            .into(profileImage)

        username.text = post.user.username
        postTitle.text = post.title
        postCreateTime.text = formatDate(post.createdAt)
        postContent.text = post.content

        Glide.with(this)
            .load("https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/" + post.headerImage)
            .into(postImage)

        val hashtagsContainer: FlexboxLayout = findViewById(R.id.hashtags_container)
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
        likeCount.text = formatCount(post.likes)
        dislikeCount.text = formatCount(post.dislikes)
        if (post.comments.isNullOrEmpty()) {
            commentCount.text = "0"
        } else {
            commentCount.text = formatCount(post.comments.size)
        }

    }

    private fun setupPostUI() {
        if (currentUserId == post.user._id) {
            editPostButton.visibility = View.VISIBLE
            editPostButton.setOnClickListener {
                openEditPostActivity()
            }
        } else {
            editPostButton.visibility = View.GONE
        }
    }

    private fun openEditPostActivity() {
        val intent = Intent(this, EditPostActivity::class.java)
        intent.putExtra("post", Gson().toJson(post))
        postDetailLauncher.launch(intent)
        finish()
    }

    private fun updateCommentsList(newComment: Comment) {
        val currentComments =
            (commentsRecyclerView.adapter as CommentFragment).comments.toMutableList()
        currentComments.add(0, newComment)

        val commentAdapter = CommentFragment(currentComments)
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
                        val updatedLikedPosts = userReactions.likedPosts?.toMutableList() ?: mutableListOf()
                        val updatedDislikedPosts = userReactions.dislikedPosts?.toMutableList() ?: mutableListOf()

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
                    Log.e("PostActivity", "Failed to update reaction: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("PostActivity", "Error updating reaction: ${t.message}", t)
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
                    Log.e("PostActivity", "Failed to refresh post: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("PostActivity", "Failed to refresh post: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Здесь можно выполнить повторный запрос к серверу
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
