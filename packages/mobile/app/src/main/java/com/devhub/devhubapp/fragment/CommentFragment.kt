package com.devhub.devhubapp.fragment

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.PostActivity
import com.devhub.devhubapp.api.CommentAPI
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CommentFragment(
    val commentsList: MutableList<Comment>,
    private val currentUserId: String,
    private val onCommentDeleted: (() -> Unit)? = null
) : RecyclerView.Adapter<CommentFragment.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: ImageView = itemView.findViewById(R.id.comment_user_avatar)
        val username: TextView = itemView.findViewById(R.id.comment_username)
        val commentTime: TextView = itemView.findViewById(R.id.comment_time)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val deleteIcon: ImageView = itemView.findViewById(R.id.comment_delete_icon)
        /*        val likeIcon: ImageView = itemView.findViewById(R.id.comment_like_icon)
                val dislikeIcon: ImageView = itemView.findViewById(R.id.comment_dislike_icon)
                val likeCount: TextView = itemView.findViewById(R.id.comment_like_count)
                val dislikeCount: TextView = itemView.findViewById(R.id.comment_dislike_count)*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        val context = holder.itemView.context

        val apiService =
            RetrofitClient.getInstance(context).getRetrofit().create(CommentAPI::class.java)

        val comment = commentsList[position]

        Glide.with(holder.itemView.context).load(comment.user.avatar).into(holder.userAvatar)
        holder.username.text = comment.user.username
        holder.commentTime.text = formatDate(comment.createdAt)
        holder.commentText.text = comment.commentText
        /*        holder.likeCount.text = comment.likes.toString()
                holder.dislikeCount.text = comment.dislikes.toString()*/

        holder.deleteIcon.visibility =
            if (comment.user._id == currentUserId) View.VISIBLE else View.GONE

        holder.deleteIcon.setOnClickListener {
            deleteComment(apiService, comment._id, position, holder.itemView)
        }
    }

    override fun getItemCount(): Int = commentsList.size

    private fun deleteComment(
        apiService: CommentAPI,
        commentId: String,
        position: Int,
        view: View
    ) {
        apiService.deleteComment(commentId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    commentsList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, commentsList.size)
                    Toast.makeText(view.context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    onCommentDeleted?.invoke()
                    updateCommentCount(view.context)
                } else {
                    Toast.makeText(view.context, "Deleting error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(view.context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCommentCount(context: Context) {
        (context as? PostActivity)?.let {
            it.commentCount.text = commentsList.size.toString()
        }
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}