package com.alec.mad.assignment1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alec.mad.assignment1.GameSingleton
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.state.GameState.PlayerCondition
import com.alec.mad.assignment1.state.GameStateObserver

/**
 * Displays a readout of the player's current points, and if they have won or lost.
 */
@SuppressLint("SetTextI18n")
class StatsBarFragment : Fragment(), GameStateObserver {

    // Views
    private lateinit var pointsReadout: TextView
    private lateinit var conditionReadout: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats_bar, container, false)

        // Get view references
        this.pointsReadout = view.findViewById(R.id.statsBarPointsReadout)
        this.conditionReadout = view.findViewById(R.id.statsBarConditionReadout)

        // Observe changes to the game state and manually update with initial values
        GameSingleton.state.observers.add(this)
        this.onUpdatePlayerPoints(GameSingleton.state.playerPoints)
        this.onUpdatePlayerCondition(GameSingleton.state.playerCondition)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Deregister self from updates
        GameSingleton.state.observers.remove(this)
    }

    override fun onUpdatePlayerPoints(playerPoints: Int) {
        this.pointsReadout.text = "Points: $playerPoints"
    }

    override fun onUpdatePlayerCondition(playerCondition: PlayerCondition) {
        this.conditionReadout.text = when (playerCondition) {
            PlayerCondition.LOST -> "You lost :("
            PlayerCondition.PLAYING -> "Reach ${GameSingleton.state.targetPoints} points to win!"
            PlayerCondition.WON -> "You won!"
        }
    }
}