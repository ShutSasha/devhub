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


class ConfirmPasswordChangeContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_password_change_container, container, false)

        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Forgot password")
        fragmentTransaction.add(R.id.title, title)

        val codeInputFragment = InputFragment()
        codeInputFragment.setInputType(InputType.TYPE_CLASS_NUMBER)
        codeInputFragment.setInputHint("Enter 6-digit code from email")
        fragmentTransaction.add(R.id.code_input_container, codeInputFragment)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        fragmentTransaction.commit()

        return view
    }

}