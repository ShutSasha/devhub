package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentLoginContainerBinding


class ForgotPasswordContainerFragment : Fragment() {

    private lateinit var binding : FragmentLoginContainerBinding

    private var emailInput: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginContainerBinding.inflate(layoutInflater, container, false)

        setUpFragment()

        return binding.root
    }

    fun setUpFragment(){
        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Forgot password")
        fragmentTransaction.add(R.id.title, title)

        val emailInputFragment = InputFragment()
        emailInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        emailInputFragment.setInputHint("Enter your email or username")
        fragmentTransaction.add(R.id.email_input_container, emailInputFragment)

        emailInputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                emailInput = text
            }
        })

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        fragmentTransaction.commit()
    }

}