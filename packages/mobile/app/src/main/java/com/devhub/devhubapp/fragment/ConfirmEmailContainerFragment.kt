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


class ConfirmEmailContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_confirm_email_container, container, false)

        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Confirming email")
        fragmentTransaction.add(R.id.title, title)

        val emailInputFragment = InputFragment()
        emailInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        emailInputFragment.setInputHint("Enter your email")
        fragmentTransaction.add(R.id.email_input_container, emailInputFragment)


        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        fragmentTransaction.commit()

        return view
    }

}