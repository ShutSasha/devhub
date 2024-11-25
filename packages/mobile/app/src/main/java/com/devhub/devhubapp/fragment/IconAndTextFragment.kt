package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devhub.devhubapp.databinding.FragmentIconAndTextBinding

class IconAndTextFragment : Fragment() {

    private lateinit var binding: FragmentIconAndTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIconAndTextBinding.inflate(inflater, container, false)

        val text = arguments?.getString("text")
        val src = arguments?.getInt("src")

        text?.let { binding.text.text = it }
        src?.let { binding.icon.setImageResource(it) }
        return binding.root
    }

    companion object {
        fun newInstance(text: String, src: Int) = IconAndTextFragment().apply {
            arguments = Bundle().apply {
                putString("text", text)
                putInt("src", src)
            }
        }
    }
}
