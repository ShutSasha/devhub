package com.devhub.devhubapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.PostFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat


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
    }

    private fun displayPost(post: Post) {

        val profile_image: ImageView = findViewById(R.id.profile_image)
        val username : TextView = findViewById(R.id.username)
        val postTitle: TextView = findViewById(R.id.post_title)
        val postContent: TextView = findViewById(R.id.post_content)
        val postImage: ImageView = findViewById(R.id.post_image)
        val like_count: TextView = findViewById(R.id.like_count)
        val dislike_count: TextView = findViewById(R.id.dislike_count)
        val comment_count: TextView = findViewById(R.id.comment_count)
        val hashtagsContainer: FlexboxLayout = findViewById(R.id.hashtags_container)

        postImage.visibility = if (post.headerImage == "") View.GONE else View.VISIBLE

        Glide.with(this)
            .load(post.user.avatar)
            .into(profile_image)

        username.text = post.user.username
        postTitle.text = post.title
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
        like_count.text = formatLikesCount(post.likes)
        dislike_count.text = formatDislikesCount(post.dislikes)
        comment_count.text = formatCommentCount(post.comments.size)
    }

    private fun formatLikesCount(likes: Int): String {
        return when {
            likes >= 1000 -> String.format("%.1fK", likes / 1000.0)
            else -> likes.toString()
        }
    }

    private fun formatDislikesCount(dislikes: Int): String {
        return when {
            dislikes >= 1000 -> String.format("%.1fK", dislikes / 1000.0)
            else -> dislikes.toString()
        }
    }

    private fun formatCommentCount(comments: Int): String {
        return when {
            comments >= 1000 -> String.format("%.1fK", comments / 1000.0)
            else -> comments.toString()
        }
    }
}
