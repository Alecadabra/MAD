package com.alec.mad.assignment1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class LayoutChanger(val notifyLayoutChange: (Int) -> Unit) : Fragment() {

    private lateinit var views: LayoutChangerViewHolder

    var orientation: Orientation = Orientation.COLUMNS
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_layout_changer, container, false)

        // Get references to views
        this.views = LayoutChangerViewHolder(
            layout1Btn = view.findViewById(R.id.layoutOneButton) as Button,
            layout2Btn = view.findViewById(R.id.layoutTwoButton) as Button,
            layout3Btn = view.findViewById(R.id.layoutThreeButton) as Button,
            orientationBtn = view.findViewById(R.id.swapOrientationButton) as Button
        )

        // TODO
        views.layout1Btn.setOnClickListener { this.notifyLayoutChange(1) }
        views.layout2Btn.setOnClickListener { this.notifyLayoutChange(2) }
        views.layout3Btn.setOnClickListener { this.notifyLayoutChange(3) }
        views.orientationBtn.setOnClickListener {  }

        return view
    }

    fun swapOrientation() {
        TODO()
    }

    data class LayoutChangerViewHolder(
        val layout1Btn: Button,
        val layout2Btn: Button,
        val layout3Btn: Button,
        val orientationBtn: Button
    )

    enum class Orientation {
        COLUMNS,
        ROWS
    }
}