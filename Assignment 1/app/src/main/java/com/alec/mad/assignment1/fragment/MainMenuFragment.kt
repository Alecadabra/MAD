package com.alec.mad.assignment1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.selector.FlagQuestionSelectorFragment
import com.alec.mad.assignment1.gameState

@SuppressLint("SetTextI18n")
class MainMenuFragment(private val message: String) : Fragment() {

    private lateinit var messageView: TextView
    private lateinit var pointsReadout: TextView
    private lateinit var startBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_starting_screen, container, false)

        this.messageView = view.findViewById(R.id.mainMenuMessage) as TextView
        this.pointsReadout = view.findViewById(R.id.mainMenuPointsReadout) as TextView
        this.startBtn = view.findViewById(R.id.startButton) as Button

        val pts = this.gameState.playerPoints
        val target = this.gameState.targetPoints

        this.messageView.text = this.message
        this.pointsReadout.text = "New Game: You have $pts points, reach $target to win"
        this.startBtn.setOnClickListener {
            // Change to flag selection screen
            activity?.supportFragmentManager?.beginTransaction()?.also { transaction ->

                // Replace the activity's fragment frame with the question selector
                transaction.replace(
                    R.id.gameFragmentFrame,
                    FlagQuestionSelectorFragment()
                )

                // Refresh the stats bar with a new one for this new game state
                transaction.replace(
                    R.id.statsBarFrame,
                    StatsBarFragment()
                )

                // Commit changes
                transaction.commit()
            } ?: throw IllegalStateException("Null activity / fragment manager")
        }

        return view
    }
}