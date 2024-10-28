package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.fragment.ConfirmPasswordChangeContainerFragment
import com.devhub.devhubapp.R


class ConfirmPasswordChangeActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirm_password_change)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confirm_password_change)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val confirmPasswordChangeContainerFragment = ConfirmPasswordChangeContainerFragment()
        fragmentTransaction.add(R.id.confirm_password_change_container, confirmPasswordChangeContainerFragment)

        fragmentTransaction.commit()
    }

}