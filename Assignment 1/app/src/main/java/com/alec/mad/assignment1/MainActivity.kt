package com.alec.mad.assignment1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alec.mad.assignment1.fragment.MainMenuFragment
import com.alec.mad.assignment1.fragment.StatsBarFragment
import com.alec.mad.assignment1.state.GameState
import com.alec.mad.assignment1.state.GameState.PlayerCondition
import com.alec.mad.assignment1.state.GameStateObserver

class MainActivity : AppCompatActivity(), GameStateObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        supportFragmentManager.also { fm ->
            fm.beginTransaction().also { transaction ->
                // Add the starting screen to the game frame
                transaction.add(
                    R.id.gameFragmentFrame,
                    MainMenuFragment("Welcome!")
                )

                // Add the stats bar frame to it's frame
                transaction.add(
                    R.id.statsBarFrame,
                    fm.findFragmentById(R.id.statsBarFrame) ?: StatsBarFragment()
                )

                // Commit changes
                transaction.commit()
            }
        }

        // Observe changes to the game state
        GameStateSingleton.state.observers.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove self from observing game state
        GameStateSingleton.state.observers.remove(this)
    }

    // Observe changes to the player condition (When they win/lose)
    override fun onUpdatePlayerCondition(playerCondition: PlayerCondition) {
        if (playerCondition != PlayerCondition.PLAYING) {

            // Clear the back stack
            repeat(supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }

            supportFragmentManager.beginTransaction().also { transaction ->
                // Swap to the main menu
                transaction.replace(
                    R.id.gameFragmentFrame,
                    MainMenuFragment(
                        when (playerCondition) {
                            PlayerCondition.PLAYING -> "" // Logically impossible
                            PlayerCondition.WON -> "You won!"
                            PlayerCondition.LOST -> "You lost :("
                        }
                    )
                )

                // Commit changes
                transaction.commit()
            }

            // Restart the game!
            GameStateSingleton.reset()
            GameStateSingleton.state.observers.add(this)
        }
    }
}