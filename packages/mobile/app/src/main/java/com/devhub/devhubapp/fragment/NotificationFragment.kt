package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.NotificationsActivity
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Notification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : Fragment() {
    private lateinit var notification: Notification
    private var isRead: Boolean = false

    companion object {
        private const val ARG_NOTIFICATION = "notification"
        private const val ARG_IS_READ = "is_read"

        fun newInstance(notification: Notification, isRead: Boolean): NotificationFragment {
            val fragment = NotificationFragment()
            val args = Bundle()
            args.putSerializable(ARG_NOTIFICATION, notification)
            args.putBoolean(ARG_IS_READ, isRead)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            notification = it.getSerializable(ARG_NOTIFICATION) as Notification
            isRead = it.getBoolean(ARG_IS_READ)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        val avatarImageView = view.findViewById<ImageView>(R.id.avatar)
        val usernameTextView = view.findViewById<TextView>(R.id.username)
        val contentTextView = view.findViewById<TextView>(R.id.content)
        val markAsReadButton = view.findViewById<ImageButton>(R.id.mark_as_read_button)

        Glide.with(this)
            .load(notification.sender.avatar)
            .into(avatarImageView)

        usernameTextView.text = notification.sender.username
        contentTextView.text = notification.content

        if (isRead) {
            markAsReadButton.setImageResource(R.drawable.ic_mark_as_read_active)
        }

        markAsReadButton.setOnClickListener {
            if (!isRead) {
                markNotificationAsRead(notification.id, markAsReadButton)
            }
        }

        return view
    }

    private fun markNotificationAsRead(notificationId: String, button: ImageButton) {
        val notificationAPI = RetrofitClient.getInstance(requireContext()).notificationAPI
        notificationAPI.markAsRead(notificationId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                if (response.isSuccessful) {
                    button.setImageResource(R.drawable.ic_mark_as_read_active)
                    (activity as? NotificationsActivity)?.moveNotificationToRead(notification)
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("NotificationFragment", "Error marking notification as read: ${t.message}", t)
            }
        })
    }
}