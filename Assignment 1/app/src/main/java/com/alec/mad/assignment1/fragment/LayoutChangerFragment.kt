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
class LayoutChangerFragment(private val layoutController: LayoutController) : Fragment(), LayoutControllerObserver {

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

        // Get references to views
        this.oneSpanBtn = view.findViewById(R.id.oneSpanButton) as Button
        this.twoSpanBtn = view.findViewById(R.id.twoSpanButton) as Button
        this.threeSpanBtn = view.findViewById(R.id.threeSpanButton) as Button
        this.orientationBtn = view.findViewById(R.id.swapOrientationButton) as Button

        this.oneSpanBtn.setOnClickListener { this.layoutController.spanCount = 1 }
        this.twoSpanBtn.setOnClickListener { this.layoutController.spanCount = 2 }
        this.threeSpanBtn.setOnClickListener { this.layoutController.spanCount = 3 }
        this.orientationBtn.setOnClickListener { this.layoutController.swapOrientation() }

        this.layoutController.observers.add(this)
        this.onUpdateOrientation(this.layoutController.orientation)
        this.onUpdateSpanCount(this.layoutController.spanCount)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        this.layoutController.observers.remove(this)
    }

    override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
        when (orientation) {
            LayoutController.HORIZONTAL -> {
                this.oneSpanBtn.text = "One Row"
                this.twoSpanBtn.text = "Two Rows"
                this.threeSpanBtn.text = "Three Rows"
                this.orientationBtn.text = "Cols"
            }
            LayoutController.VERTICAL -> {
                this.oneSpanBtn.text = "One Col"
                this.twoSpanBtn.text = "Two Cols"
                this.threeSpanBtn.text = "Three Cols"
                this.orientationBtn.text = "Rows"
            }
        }
    }
}