package com.devhub.devhubapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val textInputFragment = InputFragment()
        textInputFragment.setInputType(InputType.TYPE_CLASS_TEXT)
        textInputFragment.setInputHint("Enter your username")
        fragmentTransaction.add(R.id.text_input_container, textInputFragment)

        val emailInputFragment = InputFragment()
        emailInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        emailInputFragment.setInputHint("Enter your email")
        fragmentTransaction.add(R.id.email_input_container, emailInputFragment)

        val passwordInputFragment = InputFragment()
        passwordInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        passwordInputFragment.setInputHint("Enter your password")
        fragmentTransaction.add(R.id.password_input_container, passwordInputFragment)

        val primaryButtonFragment = PrimaryButtonFragment()
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        val line = LineFragment()
        fragmentTransaction.add(R.id.line, line)

        fragmentTransaction.commit()
    }

}