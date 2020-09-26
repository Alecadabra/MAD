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
import com.alec.mad.assignment1.state.LayoutState
import com.alec.mad.assignment1.state.LayoutStateObserver

@SuppressLint("SetTextI18n")
class LayoutChangerFragment(private val layoutState: LayoutState) : Fragment(),
    LayoutStateObserver {

    init {
        // Because the default constructor has parameters, which android does not like.
        this.retainInstance = true
    }

    // Views
    private lateinit var oneSpanBtn: Button
    private lateinit var twoSpanBtn: Button
    private lateinit var threeSpanBtn: Button
    private lateinit var orientationBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_layout_changer, container, false)

        // Get references to views
        this.oneSpanBtn = view.findViewById(R.id.oneSpanButton) as Button
        this.twoSpanBtn = view.findViewById(R.id.twoSpanButton) as Button
        this.threeSpanBtn = view.findViewById(R.id.threeSpanButton) as Button
        this.orientationBtn = view.findViewById(R.id.swapOrientationButton) as Button

        // Set button listeners
        this.oneSpanBtn.setOnClickListener { this.layoutState.spanCount = 1 }
        this.twoSpanBtn.setOnClickListener { this.layoutState.spanCount = 2 }
        this.threeSpanBtn.setOnClickListener { this.layoutState.spanCount = 3 }
        this.orientationBtn.setOnClickListener { this.layoutState.swapOrientation() }

        // Set layout controller observer and manually trigger once
        this.layoutState.observers.add(this)
        this.onUpdateOrientation(this.layoutState.orientation)
        this.onUpdateSpanCount(this.layoutState.spanCount)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.layoutState.observers.remove(this)
    }

    override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
        when (orientation) {
            LayoutState.HORIZONTAL -> {
                this.oneSpanBtn.text = "1 Row"
                this.twoSpanBtn.text = "2 Rows"
                this.threeSpanBtn.text = "3 Rows"
                this.orientationBtn.text = "Cols"
            }
            LayoutState.VERTICAL -> {
                this.oneSpanBtn.text = "1 Col"
                this.twoSpanBtn.text = "2 Cols"
                this.threeSpanBtn.text = "3 Cols"
                this.orientationBtn.text = "Rows"
            }
        }
    }
}