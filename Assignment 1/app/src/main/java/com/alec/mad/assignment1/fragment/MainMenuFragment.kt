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
class MainMenuFragment(
    /** The message to display at the top of the screen */
    private val message: String
) : Fragment() {

    init {
        // Because the default constructor has parameters, which android does not like.
        this.retainInstance = true
    }

    // Views
    private lateinit var messageView: TextView
    private lateinit var pointsReadout: TextView
    private lateinit var startBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_starting_screen, container, false)

        // Get view references
        this.messageView = view.findViewById(R.id.mainMenuMessage) as TextView
        this.pointsReadout = view.findViewById(R.id.mainMenuPointsReadout) as TextView
        this.startBtn = view.findViewById(R.id.startButton) as Button

        // Get values for points
        val pts = this.gameState.playerPoints
        val target = this.gameState.targetPoints

        // Update views
        this.messageView.text = this.message
        this.pointsReadout.text = "New Game: You have $pts points, reach $target to win"
        this.startBtn.setOnClickListener {
            // Change to flag selection screen
            fragmentManager?.also { fm ->
                fm.beginTransaction().also { transaction ->

                    // Replace the activity's fragment frame with the question selector
                    val gameFragment = fm.findFragmentById(R.id.gameFragmentFrame)
                    if (gameFragment == null || gameFragment !is FlagQuestionSelectorFragment) {
                        transaction.replace(
                            R.id.gameFragmentFrame,
                            FlagQuestionSelectorFragment()
                        )
                    }

                    // Refresh the stats bar with a new one for this new game state
                    //TODO fm.findFragmentById(R.id.statsBarFrame)?.onDestroyView()
                    transaction.replace(
                        R.id.statsBarFrame,
                        StatsBarFragment()
                    )
                    /* Old version
                    val statsBarFragment = fm.findFragmentById(R.id.statsBarFrame)
                    if (statsBarFragment == null) {
                        transaction.replace(
                            R.id.statsBarFrame,
                            StatsBarFragment()
                        )
                    }
                     */

                    // Commit changes
                    if (!transaction.isEmpty) {
                        transaction.commit()
                    }
                }
            }
        }

        return view
    }
}