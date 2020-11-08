package com.alec.mad.assignment2.view.fragment.tool

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alec.mad.assignment2.R

/**
 * A fragment to be placed in the [com.alec.mad.assignment2.view.activity.GameActivity]'s
 * bottom frame to show a simple text field describing the tool's use.
 */
class SimpleToolFragment(private var text: String? = null) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.also { bundle ->
            this.text ?: run { this.text = bundle.getString(BUNDLE_TEXT) }
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

        this.text?.also { outState.putString(BUNDLE_TEXT, it) }
    }

    companion object {
        private const val PATH = "com.alec.mad.assignment2.view.fragment.SimpleToolFragment"
        const val BUNDLE_TEXT = "$PATH.text"
    }
}