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
import com.devhub.devhubapp.databinding.FragmentConfirmEmailContainerBinding
import com.devhub.devhubapp.databinding.FragmentLoginContainerBinding


class ConfirmEmailContainerFragment : Fragment() {

    private lateinit var binding: FragmentConfirmEmailContainerBinding
    private var codeInput: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentConfirmEmailContainerBinding.inflate(layoutInflater, container, false)

        setUpFragment()

        return binding.root
    }

    private fun setUpFragment(){
        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Confirming email")
        fragmentTransaction.add(R.id.title, title)

        val codeInputFragment = InputFragment().apply {
            setInputType(InputType.TYPE_CLASS_NUMBER)
            setInputHint("Enter 6-digit code from email")
            setTextWatcher(object : InputTextListener {
                override fun onTextInputChanged(text: String) {
                    codeInput = text
                }
            })
        }
        fragmentTransaction.add(R.id.code_input_container, codeInputFragment)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        fragmentTransaction.commit()
    }

}