package com.alec.mad.assignment1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alec.mad.assignment1.fragment.MainMenuFragment
import com.alec.mad.assignment1.fragment.StatsBarFragment
import com.alec.mad.assignment1.fragment.selector.derived.FlagSpecialSelectorFragment
import com.alec.mad.assignment1.state.GameState.PlayerCondition
import com.alec.mad.assignment1.state.GameStateObserver

/**
 * The main activity. This is the only activity as the app primarily uses fragments.
 */
class MainActivity : AppCompatActivity(), GameStateObserver {

    /**
     * A listener to clear the landTabletFrame on back stack change for the land tablet special
     * case. Null if the special case does not apply.
     */
    private var landTabletListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        // Add the starting fragments to their frames
        supportFragmentManager.also { fm ->
            fm.beginTransaction().also { transaction ->
                // Add the main menu to the game frame
                val gameFragment = fm.findFragmentById(R.id.gameFragmentFrame)
                // Only if there is nothing there
                if (gameFragment == null) {
                    transaction.add(
                        R.id.gameFragmentFrame,
                        MainMenuFragment("Welcome!")
                    )
                }

                // Add the stats bar frame to it's frame
                val statsBarFragment = fm.findFragmentById(R.id.statsBarFrame)
                // Only if there is nothing there or something that is not a stats bar
                if (statsBarFragment == null || statsBarFragment !is StatsBarFragment) {
                    transaction.add(
                        R.id.statsBarFrame,
                        StatsBarFragment()
                    )
                }

                // Commit changes
                if (!transaction.isEmpty) {
                    transaction.commit()
                }
            }
        }

        // Handle landscape tablet special case
        if (resources.getBoolean(R.bool.isLandscapeTablet)) {
            this.landTabletListener = {
                supportFragmentManager.also { fm ->
                    fm.addOnBackStackChangedListener {
                        // On every change to the back stack, remove the fragment in the side frame
                        fm.findFragmentById(R.id.gameFragmentLandTabletFrame)?.also { frag ->
                            fm.beginTransaction().also { transaction ->
                                transaction.remove(frag)
                                transaction.commit()
                            }
                        }
                    }
                }
            }
            // Get local reference to the listener property for thread safety
            val localLandTabletListener: () -> Unit = this.landTabletListener
                ?: throw ConcurrentModificationException(
                    "Listener var to null changed by another thread"
                )
            supportFragmentManager.addOnBackStackChangedListener(localLandTabletListener)
        } else {
            // Special case does not apply
            this.landTabletListener = null
        }

        // Observe changes to the game state
        GameSingleton.state.observers.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove special case back stack listener if non null
        this.landTabletListener?.also { listener ->
            supportFragmentManager.removeOnBackStackChangedListener(listener)
        }

        // Remove self from observing game state
        GameSingleton.state.observers.remove(this)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.gameFragmentFrame)
        // Block back button presses when the user is selecting a flag from isSpecial
        if (currentFragment !is FlagSpecialSelectorFragment) {
            super.onBackPressed()
        }
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
                            PlayerCondition.WON -> "You won!"
                            PlayerCondition.LOST -> "You lost :("
                            PlayerCondition.PLAYING -> throw ConcurrentModificationException(
                                "Player condition changed by another thread"
                            )
                        }
                    )
                )

                // Commit changes
                transaction.commit()
            }

            // Restart the game!
            GameSingleton.state.observers.remove(this)
            GameSingleton.reset()
            GameSingleton.state.observers.add(this)
        }
    }
}