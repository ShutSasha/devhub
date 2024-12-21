package com.devhub.devhubapp.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.CreatePostActivity
import com.devhub.devhubapp.activity.MainActivity
import com.devhub.devhubapp.activity.UserProfileActivity
import com.devhub.devhubapp.classes.EncryptedPreferencesManager

class HeaderFragment : Fragment() {
    private val REQUEST_CODE_CREATE_POST = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_header, container, false)

        val burgerMenu = view.findViewById<ImageView>(R.id.burger_menu)
        val avatarImageView = view.findViewById<ImageView>(R.id.user_avatar)
        val createPostButton = view.findViewById<ImageView>(R.id.create_post_button)
        val avatar = view.findViewById<ImageView>(R.id.user_avatar)

        val encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        val user = encryptedPreferencesManager.getUserData()

        if (user.avatar.isNotEmpty()) {
            Glide.with(this)
                .load(user.avatar)
                .into(avatarImageView)
        }

        createPostButton.setOnClickListener {
            val intent = Intent(activity, CreatePostActivity::class.java)
            createPostLauncher.launch(intent)
        }

        avatar.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("USER_ID", user._id)
            startActivity(intent)
        }

        burgerMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        return view
    }

    private val createPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            (activity as? MainActivity)?.refreshPosts()
        }
    }
}
