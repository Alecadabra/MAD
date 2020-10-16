package com.alec.mad.assignment2.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alec.mad.assignment2.R

class SimpleToolFragment : Fragment() {

    lateinit var text: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.also { bundle ->
            bundle.getString(BUNDLE_TEXT)?.also {
                this.text = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_simple_tool, container, false)

        view.findViewById<TextView>(R.id.simpleToolText)?.text = this.text

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.also { bundle ->
            if (this::text.isInitialized) {
                bundle.putString(BUNDLE_TEXT, this.text)
            }
        }
    }

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment2.view.fragment"
        const val BUNDLE_TEXT = "$PACKAGE.text"
    }
}