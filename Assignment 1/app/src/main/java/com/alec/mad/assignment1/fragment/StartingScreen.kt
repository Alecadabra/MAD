package com.alec.mad.assignment1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.alec.mad.assignment1.singleton.GameState
import com.alec.mad.assignment1.R

@SuppressLint("SetTextI18n")
class StartingScreen : Fragment() {

    private lateinit var pointsReadout: TextView
    private lateinit var startBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_starting_screen, container, false)

        this.pointsReadout = view.findViewById(R.id.pointsReadout) as TextView
        this.startBtn = view.findViewById(R.id.startButton) as Button

        val pts = GameState.playerPoints
        val target = GameState.targetPoints

        this.pointsReadout.text = "You have $pts points and must reach $target to win"
        this.startBtn.setOnClickListener {
            TODO("Change to flag selector fragment")
        }

        return view
    }
}