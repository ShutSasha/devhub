package com.devhub.devhubapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentErrorBinding
import com.devhub.devhubapp.databinding.FragmentRegistrationContainerBinding
import org.w3c.dom.Text

class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErrorBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    fun setErrorText(errorText: String){
        binding.errorTextView.text = errorText
    }

}