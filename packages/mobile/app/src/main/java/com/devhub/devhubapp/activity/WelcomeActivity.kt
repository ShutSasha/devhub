package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.fragment.PrimaryButtonFragment

class WelcomeActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")

    lateinit var logIn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val createAnAccountFragment = PrimaryButtonFragment()
        createAnAccountFragment.setButtonText("Create an account")
        createAnAccountFragment.setButtonAction {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        fragmentTransaction.add(R.id.create_an_account_container, createAnAccountFragment)

        fragmentTransaction.commit()

        logIn = findViewById(R.id.login_button)

        logIn.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LogInActivity::class.java)
            startActivity(intent)
        }
    }
}