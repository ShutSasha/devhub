package com.devhub.devhubapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.MainActivity
import com.devhub.devhubapp.activity.PostActivity
import com.devhub.devhubapp.activity.UserProfileActivity
import com.devhub.devhubapp.dataClasses.Post
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson

class PostFragment : Fragment() {
    private lateinit var post: Post
    private lateinit var avatar: ImageView
    private lateinit var username: TextView
    private lateinit var hashtagsContainer: FlexboxLayout
    private lateinit var postTitle: TextView
    private lateinit var postImage: ImageView
    private lateinit var likeCountTextView: TextView
    private lateinit var dislikeCountTextView: TextView
    private lateinit var commentCountTextView: TextView
    private lateinit var commentIcon: ImageView

    private val postDetailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val shouldUpdate = result.data?.getBooleanExtra("UPDATE_POSTS", false) ?: false
                if (shouldUpdate) {
                    (activity as? MainActivity)?.refreshPosts()
                }
            }
        }

    companion object {
        private const val ARG_POST = "arg_post"
        private const val BASE_URL = "https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/"

        fun newInstance(post: Post): PostFragment {
            val fragment = PostFragment()
            val args = Bundle().apply {
                putString(ARG_POST, Gson().toJson(post))
            }

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        arguments?.getString(ARG_POST)?.let {
            post = Gson().fromJson(it, Post::class.java)
        }

        view.setOnClickListener {
            openPostDetailActivity()
        }


        avatar = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.username)
        hashtagsContainer = view.findViewById(R.id.hashtags_container)
        postTitle = view.findViewById(R.id.post_title)
        likeCountTextView = view.findViewById(R.id.like_count)
        dislikeCountTextView = view.findViewById(R.id.dislike_count)
        commentCountTextView = view.findViewById(R.id.comment_count)
        commentIcon = view.findViewById(R.id.comment_icon)
        postImage = view.findViewById(R.id.post_image)

        arguments?.getString(ARG_POST)?.let {
            val post = Gson().fromJson(it, Post::class.java)
            postImage.visibility = if (post.headerImage == "") View.GONE else View.VISIBLE

            displayPost(post)
        }

        return view
    }

    private fun openPostDetailActivity() {
        val intent = Intent(requireContext(), PostActivity::class.java)
        intent.putExtra("post", Gson().toJson(post))
        postDetailLauncher.launch(intent)
    }

    private fun displayPost(post: Post) {

        Glide.with(this)
            .load(post.user.avatar)
            .into(avatar)

        username.text = post.user.username

        postTitle.text = post.title

        Glide.with(this)
            .load(BASE_URL + post.headerImage)
            .into(postImage)

        hashtagsContainer.removeAllViews()
        post.tags?.forEach { tag ->
            val textView = TextView(context)
            textView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 20
            }
            textView.text = "#$tag"
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            textView.textSize = 14f
            textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold_font)
            hashtagsContainer.addView(textView)
        }
        likeCountTextView.text = formatCount(post.likes)
        dislikeCountTextView.text = formatCount(post.dislikes)
        if (post.comments != null && post.comments.isNotEmpty()) {
            commentCountTextView.text = formatCount(post.comments.size)
        } else {
            commentCountTextView.visibility = View.GONE
            commentIcon.visibility = View.GONE
        }

        avatar.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            Log.e("UserId", post.toString())
            intent.putExtra("USER_ID", post.user._id)
            startActivity(intent)
        }

    }

    private fun formatCount(reaction: Int): String {
        return when {
            reaction >= 1000 -> String.format("%.1fK", reaction / 1000.0)
            else -> reaction.toString()
        }
    }
}
