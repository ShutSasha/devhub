package com.devhub.devhubapp.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val userAvatar = findViewById<ImageView>(R.id.user_avatar)
        val username = findViewById<TextView>(R.id.username)
        val createPostButton = findViewById<TextView>(R.id.create_post_button)
        val addBackgroundButton = findViewById<FrameLayout>(R.id.add_background_button)
        val postTitle = findViewById<EditText>(R.id.title_input)
        val postTags = findViewById<EditText>(R.id.tags_input)
        val postContent = findViewById<EditText>(R.id.content_input)

        val usernameText = intent.getStringExtra("USERNAME") ?: "@username"
        val userAvatarUrl = intent.getStringExtra("USER_AVATAR")

        username.text = usernameText

        if (!userAvatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(userAvatarUrl)
                .into(userAvatar)
        }

        val inflater = LayoutInflater.from(this)
        val customButtonLayout = inflater.inflate(R.layout.fragment_add_background_button, null)
        addBackgroundButton.addView(customButtonLayout)

        addBackgroundButton.setOnClickListener {
        }

        createPostButton.setOnClickListener {
        }
    }
}
