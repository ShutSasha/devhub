package com.devhub.devhubapp.activity

import CommentFragment
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale


class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            fragmentTransaction.replace(R.id.header_container_post, HeaderFragment())
            fragmentTransaction.replace(R.id.footer_container_post, FooterFragment())
            fragmentTransaction.commit()
        }

        val postJson = intent.getStringExtra("post")
        val post = Gson().fromJson(postJson, Post::class.java)

        post?.let { displayPost(it) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.post)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<LinearLayout>(R.id.back_button_container).setOnClickListener {
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
        /*        val starCount: TextView = findViewById(R.id.star_count)*/
        val commentCount: TextView = findViewById(R.id.comment_count)
        val hashtagsContainer: FlexboxLayout = findViewById(R.id.hashtags_container)
        val commentsRecyclerView: RecyclerView = findViewById(R.id.comments_recycler_view)
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        val commentAdapter = CommentFragment(post.comments)
        commentsRecyclerView.adapter = commentAdapter

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
        /*        starCount.text = formatCount(post.stars)*/
        commentCount.text = formatCount(post.comments.size)
    }

    private fun formatCount(reaction: Int): String {
        return when {
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
