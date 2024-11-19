package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.CommentAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.AddCommentRequest
import com.devhub.devhubapp.dataClasses.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCommentFragment : Fragment() {

    private lateinit var userAvatar: ImageView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: Button
    private lateinit var preferencesManager: EncryptedPreferencesManager
    private lateinit var commentAPI: CommentAPI

    var onCommentAdded: ((Comment) -> Unit)? = null

    private var postId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_comment, container, false)

        userAvatar = view.findViewById(R.id.user_avatar)
        commentInput = view.findViewById(R.id.comment_input)
        sendButton = view.findViewById(R.id.send_button)

        preferencesManager = EncryptedPreferencesManager(requireContext())
        commentAPI = RetrofitClient.getInstance(requireContext()).commentAPI

        val currentUser = preferencesManager.getUserData()
        Glide.with(this).load(currentUser.avatar).into(userAvatar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getString("postId")

        sendButton.isEnabled = false

        commentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendButton.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        sendButton.setOnClickListener {
            val content = commentInput.text.toString().trim()
            if (postId != null) {
                handleCommentSend(content)
            } else {
                Log.e("AddCommentFragment", "postId is null")
            }
        }
    }

    private fun handleCommentSend(content: String) {
        val currentUser = preferencesManager.getUserData()

        val createCommentRequest = AddCommentRequest(
            postId = postId ?: return,
            content = content,
            userId = currentUser._id
        )

        commentAPI.createComment(createCommentRequest).enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    val newComment = response.body()
                    if (newComment != null) {
                        onCommentAdded?.invoke(newComment)
                        commentInput.text.clear()
                    }
                } else {
                    Log.e(
                        "AddCommentFragment",
                        "Ошибка при отправке комментария: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.e("AddCommentFragment", "Не удалось отправить комментарий")
            }
        })
    }
}