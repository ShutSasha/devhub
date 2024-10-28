package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.fragment.ConfirmEmailContainerFragment
import com.devhub.devhubapp.R

class ConfirmEmailActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirm_email)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confirm_email)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val confirmEmailContainerFragment = ConfirmEmailContainerFragment()
        fragmentTransaction.add(R.id.confirm_email_container, confirmEmailContainerFragment)

        fragmentTransaction.commit()
    }

}