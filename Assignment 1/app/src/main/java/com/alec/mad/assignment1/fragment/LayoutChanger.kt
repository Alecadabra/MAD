package com.alec.mad.assignment1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.singleton.LayoutController
import com.alec.mad.assignment1.singleton.LayoutControllerObserver

@SuppressLint("SetTextI18n")
class LayoutChanger() : Fragment(), LayoutControllerObserver {

    private lateinit var oneSpanBtn: Button
    private lateinit var twoSpanBtn: Button
    private lateinit var threeSpanBtn: Button
    private lateinit var orientationBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_layout_changer, container, false)

        LayoutController.observers.add(this)

        // Get references to views
        this.oneSpanBtn = view.findViewById(R.id.layoutOneButton) as Button
        this.twoSpanBtn = view.findViewById(R.id.layoutTwoButton) as Button
        this.threeSpanBtn = view.findViewById(R.id.layoutThreeButton) as Button
        this.orientationBtn = view.findViewById(R.id.swapOrientationButton) as Button

        this.oneSpanBtn.setOnClickListener { LayoutController.spanCount = 1 }
        this.twoSpanBtn.setOnClickListener { LayoutController.spanCount = 2 }
        this.threeSpanBtn.setOnClickListener { LayoutController.spanCount = 3 }
        this.orientationBtn.setOnClickListener { LayoutController.swapOrientation() }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        LayoutController.observers.remove(this)
    }

    override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
        when (orientation) {
            LayoutController.HORIZONTAL -> {
                this.oneSpanBtn.text = "One Column"
                this.twoSpanBtn.text = "Two Columns"
                this.threeSpanBtn.text = "Three Columns"
                this.orientationBtn.text = "Rows"
            }
            LayoutController.VERTICAL -> {
                this.oneSpanBtn.text = "One Row"
                this.twoSpanBtn.text = "Two Rows"
                this.threeSpanBtn.text = "Three Rows"
                this.orientationBtn.text = "Columns"
            }
        }
    }
}