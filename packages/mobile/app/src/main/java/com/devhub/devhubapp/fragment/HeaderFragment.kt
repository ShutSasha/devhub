package com.devhub.devhubapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.CreatePostActivity
import com.devhub.devhubapp.classes.EncryptedPreferencesManager

class HeaderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_header, container, false)

        val avatarImageView = view.findViewById<ImageView>(R.id.user_avatar)
        val createPostButton = view.findViewById<ImageView>(R.id.create_post_button)

        val encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        val user = encryptedPreferencesManager.getUserData()

        if (user.avatar.isNotEmpty()) {
            Glide.with(this)
                .load(user.avatar)
                .into(avatarImageView)
        }

        createPostButton.setOnClickListener {
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
