package com.devhub.devhubapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentPrimaryButtonBinding
import com.devhub.devhubapp.databinding.FragmentTitleBinding


class PrimaryButtonFragment : Fragment() {

    private lateinit var button: Button
    private var text: String? = null
    private var buttonAction: (() -> Unit)? = null
    private lateinit var binding: FragmentPrimaryButtonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrimaryButtonBinding.inflate(layoutInflater, container, false)

        button = binding.primarybtn
        button.text = this.text

        button.setOnClickListener {
            buttonAction?.invoke()
        }

        return binding.root
    }

    fun setButtonText(text: String) {
        this.text = text
        if (::button.isInitialized) {
            button.text = text
        }
    }

    fun setButtonAction(action: () -> Unit) {
        this.buttonAction = action
    }

//    fun setHeightAndWidth(heightSize: String, widthSize: String){
//        binding.primarybtn.height = heightSize
//
//    }
}
