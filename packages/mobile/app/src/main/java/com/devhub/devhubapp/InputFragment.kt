package com.devhub.devhubapp

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText


class InputFragment : Fragment() {

    private lateinit var editText: EditText
    private var inputType: Int = InputType.TYPE_CLASS_TEXT
    private var hintText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)

        editText = view.findViewById(R.id.edit_text)
        editText.inputType = inputType
        editText.hint = hintText

        return view
    }

    fun setInputType(type: Int) {
        inputType = type
    }

    fun setInputHint(text: String) {
        hintText = text
    }

}