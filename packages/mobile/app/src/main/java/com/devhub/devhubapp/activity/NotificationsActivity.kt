package com.devhub.devhubapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Notification
import com.devhub.devhubapp.dataClasses.NotificationResponse
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.NotificationFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView

class NotificationsActivity : AppCompatActivity(), DrawerHandler {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var unreadContainer: LinearLayout
    private lateinit var readContainer: LinearLayout
    private lateinit var noUnreadTextView: TextView
    private lateinit var noReadTextView: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val displayMetrics = resources.displayMetrics
        navigationView.layoutParams.width = displayMetrics.widthPixels
        navigationView.requestLayout()

        unreadContainer = findViewById(R.id.unread_notifications)
        readContainer = findViewById(R.id.read_notifications)
        noUnreadTextView = findViewById(R.id.no_unread_notifications)
        noReadTextView = findViewById(R.id.no_read_notifications)

        val encryptedPreferencesManager = EncryptedPreferencesManager(this)
        val userId = encryptedPreferencesManager.getUserData()._id

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notifications)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val footerFragment = FooterFragment.newInstance("home")
            supportFragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, footerFragment)
                .commit()
        }

        fetchNotifications(userId)

        setupDrawer()
    }

    private fun setupDrawer() {
        val headerView = navigationView.getHeaderView(0)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.nav_user_avatar)
        val closeImageView = headerView.findViewById<ImageView>(R.id.nav_close)

        val user = EncryptedPreferencesManager(this).getUserData()
        if (user.avatar.isNotEmpty()) {
            Glide.with(this)
                .load(user.avatar)
                .into(avatarImageView)
        }

        closeImageView.setOnClickListener {
            drawerLayout.closeDrawers()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_my_posts -> {
                    // Handle My Posts action
                    true
                }

                R.id.nav_notifications -> {
                    val intent = Intent(this, NotificationsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_logout -> {
                    encryptedPreferencesManager.deleteUserData()
                    finish()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun openDrawer() {
        drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }

    private fun fetchNotifications(userId: String) {
        val notificationAPI = RetrofitClient.getInstance(this).notificationAPI
        notificationAPI.getNotifications(userId).enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    if (notifications != null) {
                        displayNotifications(notifications)
                    }
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Log.e("NotificationsActivity", "Error fetching notifications: ${t.message}", t)
            }
        })
    }

    private fun displayNotifications(notifications: NotificationResponse) {
        if (notifications.unread.isEmpty()) {
            noUnreadTextView.visibility = View.VISIBLE
        } else {
            noUnreadTextView.visibility = View.GONE
            notifications.unread.forEach {
                val fragment = NotificationFragment.newInstance(it, false)
                supportFragmentManager.beginTransaction()
                    .add(unreadContainer.id, fragment, it.id)
                    .commit()
            }
        }

        if (notifications.read.isEmpty()) {
            noReadTextView.visibility = View.VISIBLE
        } else {
            noReadTextView.visibility = View.GONE
            notifications.read.forEach {
                val fragment = NotificationFragment.newInstance(it, true)
                supportFragmentManager.beginTransaction()
                    .add(readContainer.id, fragment, it.id)
                    .commit()
            }
        }
    }

    fun moveNotificationToRead(notification: Notification) {
        val fragment = supportFragmentManager.findFragmentByTag(notification.id)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commitNow()

            val newFragment = NotificationFragment.newInstance(notification, true)
            supportFragmentManager.beginTransaction()
                .add(readContainer.id, newFragment, notification.id)
                .commit()

            if (unreadContainer.childCount == 0) {
                noUnreadTextView.visibility = View.VISIBLE
            }
            noReadTextView.visibility = View.GONE
        }
    }
}