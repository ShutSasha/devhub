package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R

class AddCommentFragment : Fragment() {

    private lateinit var userAvatar: ImageView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_comment, container, false)

        userAvatar = view.findViewById(R.id.user_avatar)
        commentInput = view.findViewById(R.id.comment_input)
        sendButton = view.findViewById(R.id.send_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(R.drawable.ic_pfp)
            .into(userAvatar)

        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()

            if (commentText.isNotEmpty()) {
                handleCommentSend(commentText)
                commentInput.text.clear()
            }
        }
    }

    private fun handleCommentSend(commentText: String) {
    }
}
