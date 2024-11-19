package com.devhub.devhubapp.fragment

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.dataClasses.Comment
import java.util.Locale

class CommentFragment(val comments: List<Comment>) :
    RecyclerView.Adapter<CommentFragment.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: ImageView = itemView.findViewById(R.id.comment_user_avatar)
        val username: TextView = itemView.findViewById(R.id.comment_username)
        val commentTime: TextView = itemView.findViewById(R.id.comment_time)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val likeIcon: ImageView = itemView.findViewById(R.id.comment_like_icon)
        val dislikeIcon: ImageView = itemView.findViewById(R.id.comment_dislike_icon)
        val likeCount: TextView = itemView.findViewById(R.id.comment_like_count)
        val dislikeCount: TextView = itemView.findViewById(R.id.comment_dislike_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.topMargin = if (position == 0) 0 else 20
        holder.itemView.layoutParams = layoutParams

        Glide.with(holder.itemView.context).load(comment.user.avatar).into(holder.userAvatar)

        holder.username.text = comment.user.username
        holder.commentTime.text = formatDate(comment.createdAt)
        holder.commentText.text = comment.commentText
        holder.likeCount.text = comment.likes.toString()
        holder.dislikeCount.text = comment.dislikes.toString()
    }

    override fun getItemCount(): Int = comments.size

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }
}