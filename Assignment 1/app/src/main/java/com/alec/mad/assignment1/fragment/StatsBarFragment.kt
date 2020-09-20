package com.alec.mad.assignment1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alec.mad.assignment1.singleton.GameState
import com.alec.mad.assignment1.singleton.GameState.PlayerCondition
import com.alec.mad.assignment1.R

@SuppressLint("SetTextI18n")
class StatsBarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_stats_bar, container, false)

        // Give companion object reference to points readout field and update text
        singletonPtsReference = view.findViewById(R.id.statsBarPointsReadout) as TextView
        onUpdatePoints()

        return view
    }

    companion object {
        /**
         * Singleton's reference to the text view so that it can be statically accessed by
         * onUpdatePoints.
         */
        private lateinit var singletonPtsReference: TextView

        /**
         * Call this whenever the value of points changes.
         */
        fun onUpdatePoints() {
            val pts = GameState.playerPoints
            val remaining = GameState.targetPoints - pts
            val playerCondition = GameState.playerCondition

            singletonPtsReference.text = when (playerCondition) {
                PlayerCondition.LOST -> "You are dead. No big surprise"
                PlayerCondition.PLAYING -> "You have $pts points, $remaining more to win!"
                PlayerCondition.WON -> "You won!"
            }
        }
    }

}