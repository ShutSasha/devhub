package com.devhub.devhubapp

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class LoginContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login_container, container, false)

        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Log In")
        fragmentTransaction.add(R.id.title_container, title)

        val textInputFragment = InputFragment()
        textInputFragment.setInputType(InputType.TYPE_CLASS_TEXT)
        textInputFragment.setInputHint("Enter your email or username")
        fragmentTransaction.add(R.id.username_and_email_input_container, textInputFragment)

        val passwordInputFragment = InputFragment()
        passwordInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        passwordInputFragment.setInputHint("Enter your password")
        fragmentTransaction.add(R.id.password_input_container, passwordInputFragment)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        val lineFragment = LineFragment()
        fragmentTransaction.add(R.id.line_container, lineFragment)

        val textAndLinkFragment = TextWithLinkFragment()
        textAndLinkFragment.setTextAndLinkText(
            getString(R.string.dont_have_an_account),
            getString(R.string.sign_in)
        )
        fragmentTransaction.add(R.id.text_and_linkText_container, textAndLinkFragment)

        fragmentTransaction.commit()

        return view
    }

}