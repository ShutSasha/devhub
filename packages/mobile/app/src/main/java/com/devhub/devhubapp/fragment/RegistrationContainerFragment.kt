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


class RegistrationContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_registration_container, container, false)

        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Registration")
        fragmentTransaction.add(R.id.title, title)

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
        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        val lineFragment = LineFragment()
        fragmentTransaction.add(R.id.line, lineFragment)

        val textAndLinkFragment = TextWithLinkFragment()
        textAndLinkFragment.setTextAndLinkText(
            getString(R.string.already_have_an_account),
            getString(R.string.log_in)
        )
        fragmentTransaction.add(R.id.textAndLinkText, textAndLinkFragment)

        fragmentTransaction.commit()

        return view
    }

}