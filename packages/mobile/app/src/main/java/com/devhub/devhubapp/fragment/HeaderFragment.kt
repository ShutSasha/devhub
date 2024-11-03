package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R


class HeaderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_header, container, false)


        val avatarImageView = view.findViewById<ImageView>(R.id.user_avatar)
        val userAvatarUrl = arguments?.getString("USER_AVATAR")

        if (!userAvatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(userAvatarUrl)
                .placeholder(R.drawable.people_brainstorming_idea)
                .into(avatarImageView)
        }

        return view
    }
}
