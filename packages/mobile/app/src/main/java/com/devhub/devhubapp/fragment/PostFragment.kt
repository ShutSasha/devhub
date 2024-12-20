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
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.SavedPostDetailsResponse
import com.devhub.devhubapp.dataClasses.SavedPostRequest
import com.devhub.devhubapp.dataClasses.UserIdRequest
import com.devhub.devhubapp.dataClasses.UserReactions
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private lateinit var likedPosts: List<String>
    private lateinit var dislikedPosts: List<String>
    private lateinit var starIcon: ImageView
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager

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
        private const val ARG_LIKED_POSTS = "arg_liked_posts"
        private const val ARG_DISLIKED_POSTS = "arg_disliked_posts"
        private const val BASE_URL = "https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/"

        fun newInstance(post: Post, reactions: UserReactions? = null): PostFragment {
            val fragment = PostFragment()
            val args = Bundle().apply {
                putString(ARG_POST, Gson().toJson(post))
                putStringArrayList(ARG_LIKED_POSTS, ArrayList(reactions?.likedPosts ?: emptyList()))
                putStringArrayList(
                    ARG_DISLIKED_POSTS,
                    ArrayList(reactions?.dislikedPosts ?: emptyList())
                )
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_POST)?.let {
            post = Gson().fromJson(it, Post::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        starIcon = view.findViewById(R.id.star_icon)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())

        val savedPostIds = encryptedPreferencesManager.getUserSavedPosts()

        val isSaved = savedPostIds.contains(post._id)
        starIcon.setImageResource(if (isSaved) R.drawable.ic_star_active else R.drawable.ic_star)

        starIcon.setOnClickListener {
            toggleSavePost(post._id)
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

        displayPost(post)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        avatar = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.username)
        hashtagsContainer = view.findViewById(R.id.hashtags_container)
        postTitle = view.findViewById(R.id.post_title)
        likeCountTextView = view.findViewById(R.id.like_count)
        dislikeCountTextView = view.findViewById(R.id.dislike_count)
        commentCountTextView = view.findViewById(R.id.comment_count)
        commentIcon = view.findViewById(R.id.comment_icon)
        postImage = view.findViewById(R.id.post_image)

        val likeIcon = view.findViewById<ImageView>(R.id.like_icon)
        val dislikeIcon = view.findViewById<ImageView>(R.id.dislike_icon)

        likeIcon.setOnClickListener {
            toggleLike(post._id)
        }

        dislikeIcon.setOnClickListener {
            toggleDislike(post._id)
        }

        displayPost(post)
    }

    private fun toggleSavePost(postId: String) {
        val userId = encryptedPreferencesManager.getUserData()._id
        val request = SavedPostRequest(userId, postId)

        RetrofitClient.getInstance(requireContext()).userAPI.toggleSavePost(request)
            .enqueue(object : Callback<SavedPostDetailsResponse> {
                override fun onResponse(call: Call<SavedPostDetailsResponse>, response: Response<SavedPostDetailsResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedPost ->
                            post.saved = updatedPost.saved
                            val savedPostIds =
                                encryptedPreferencesManager.getUserSavedPosts().toMutableList()
                            if (savedPostIds.contains(postId)) {
                                savedPostIds.remove(postId)
                                starIcon.setImageResource(R.drawable.ic_star)
                            } else {
                                savedPostIds.add(postId)
                                starIcon.setImageResource(R.drawable.ic_star_active)
                            }
                            encryptedPreferencesManager.saveUserSavedPosts(savedPostIds)
                        }
                    } else {
                        Log.e(
                            "PostFragment",
                            "Failed to toggle save post: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<SavedPostDetailsResponse>, t: Throwable) {
                    Log.e("PostFragment", "Error toggling save post: ${t.message}", t)
                }
            })
    }

    private fun toggleLike(postId: String) {
        val userId = encryptedPreferencesManager.getUserData()._id

        val request = UserIdRequest(userId)
        RetrofitClient.getInstance(requireContext()).postAPI.likePost(postId, request)
            .enqueue(object :
                Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            post.likes = it.likes
                            post.dislikes = it.dislikes

                            val reactions =
                                encryptedPreferencesManager.getUserReactions()
                            val updatedLikedPosts = reactions.likedPosts?.toMutableList()
                            val updatedDislikedPosts = reactions.dislikedPosts?.toMutableList()

                            if (updatedLikedPosts != null) {
                                if (updatedLikedPosts.contains(postId)) {
                                    updatedLikedPosts.remove(postId)
                                } else {
                                    updatedLikedPosts.add(postId)
                                    if (updatedDislikedPosts != null) {
                                        updatedDislikedPosts.remove(postId)
                                    }
                                }
                            }

                            val updatedReactions = reactions.copy(
                                likedPosts = updatedLikedPosts,
                                dislikedPosts = updatedDislikedPosts
                            )
                            encryptedPreferencesManager.saveUserReactions(
                                updatedReactions
                            )

                            updateReactionIcons(it)
                        }
                    } else {
                        Log.e(
                            "PostFragment",
                            "Failed to like post: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.e("PostFragment", "Error liking post: ${t.message}", t)
                }
            })
    }

    private fun toggleDislike(postId: String) {
        val userId = encryptedPreferencesManager.getUserData()._id

        val request = UserIdRequest(userId)
        RetrofitClient.getInstance(requireContext()).postAPI.dislikePost(postId, request)
            .enqueue(object :
                Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            post.likes = it.likes
                            post.dislikes = it.dislikes

                            val reactions =
                                encryptedPreferencesManager.getUserReactions()
                            val updatedLikedPosts = reactions.likedPosts?.toMutableList()
                            val updatedDislikedPosts = reactions.dislikedPosts?.toMutableList()

                            if (updatedDislikedPosts != null) {
                                if (updatedDislikedPosts.contains(postId)) {
                                    updatedDislikedPosts.remove(postId)
                                } else {
                                    updatedDislikedPosts.add(postId)
                                    if (updatedLikedPosts != null) {
                                        updatedLikedPosts.remove(postId)
                                    }
                                }
                            }

                            val updatedReactions = reactions.copy(
                                likedPosts = updatedLikedPosts,
                                dislikedPosts = updatedDislikedPosts
                            )
                            encryptedPreferencesManager.saveUserReactions(
                                updatedReactions
                            )

                            updateReactionIcons(it)
                        }
                    } else {
                        Log.e(
                            "PostFragment",
                            "Failed to dislike post: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.e("PostFragment", "Error disliking post: ${t.message}", t)
                }
            })
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

        if (post.headerImage.isNullOrEmpty()) {
            postImage.visibility = View.GONE
        } else {
            postImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(BASE_URL + post.headerImage)
                .into(postImage)
        }

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

        updateReactionIcons(post)

        if (post.comments.isNullOrEmpty()) {
            commentCountTextView.text = "0"
            commentCountTextView.visibility = View.VISIBLE
            commentIcon.visibility = View.VISIBLE
        } else {
            commentCountTextView.text = formatCount(post.comments.size)
            commentCountTextView.visibility = View.VISIBLE
            commentIcon.visibility = View.VISIBLE
        }

        avatar.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            Log.e("UserId", post.toString())
            intent.putExtra("USER_ID", post.user._id)
            startActivity(intent)
        }
    }

    private fun updateReactionIcons(post: Post) {
        val likeIcon = view?.findViewById<ImageView>(R.id.like_icon)
        val dislikeIcon = view?.findViewById<ImageView>(R.id.dislike_icon)

        val userReactions = encryptedPreferencesManager.getUserReactions()

        val isLiked = userReactions.likedPosts?.contains(post._id) == true
        val isDisliked = userReactions.dislikedPosts?.contains(post._id) == true

        likeIcon?.setImageResource(if (isLiked) R.drawable.ic_like_active else R.drawable.ic_like)
        dislikeIcon?.setImageResource(if (isDisliked) R.drawable.ic_dislike_active else R.drawable.ic_dislike)

        likeCountTextView.text = formatCount(post.likes)
        dislikeCountTextView.text = formatCount(post.dislikes)
    }

    private fun formatCount(reaction: Int): String {
        return when {
            reaction >= 1000 -> String.format("%.1fK", reaction / 1000.0)
            else -> reaction.toString()
        }
    }
}