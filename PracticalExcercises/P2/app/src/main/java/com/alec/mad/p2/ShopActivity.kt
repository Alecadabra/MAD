package com.alec.mad.p2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_shop.*
import kotlin.properties.Delegates

class ShopActivity : AppCompatActivity() {

    private val items: MutableList<Item> = GameState.gameMap[GameState.player].items

    private var idx: Int by Delegates.observable(0) {_, _, _ -> update()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        prev.setOnClickListener { idx-- }

        next.setOnClickListener { idx++ }

        buy.setOnClickListener {
            val player = GameState.player
            val item = items[idx]

            // Remove cash from player
            player.cash -= item.value

            // Give item to player
            when (item) {
                is Food -> player.health = (player.health + item.health).coerceAtMost(GameState.PLAYER_MAX_HEALTH)
                is Equipment -> player.equipment.add(item)
            }

            // Remove item from shop
            items.remove(items[idx])
            if (items.size == 0) {
                @SuppressLint("SetTextI18n")
                counter.text = "No items left"
                description.text = ""
            }
            else {
                // Adjust shop index
                idx = 0.coerceAtLeast(idx - 1)
            }

            // Death check
            @SuppressLint("SetTextI18n")
            if (player.health <= 0) {
                counter.text = "You are dead"
                description.text = "No big surprise"
                buy.text = "Restart game"
                prev.apply {
                    isEnabled = false
                    setTextColor(Color.GRAY)
                }
                next.apply {
                    isEnabled = false
                    setTextColor(Color.GRAY)
                }
                buy.setOnClickListener {
                    GameState.resetGame()
                    startActivity(NavigationActivity.getIntent(this))
                }

                setResult(Activity.RESULT_OK, theyDied())
            }
            else update()
        }

        update()
    }

    @SuppressLint("SetTextI18n")
    private fun update() {
        if(items.size == 0) {
            counter.text = "No items left"
            description.text = intent.getStringExtra(IntentHandler.SOME_STRING)
            prev.apply {
                isEnabled = false
                setTextColor(Color.GRAY)
            }
            next.apply {
                isEnabled = false
                setTextColor(Color.GRAY)
            }
            buy.apply {
                isEnabled = false
                setTextColor(Color.GRAY)
            }
        }
        else {
            counter.text = "Item ${idx + 1} of ${items.size}"
            description.text = items[idx].toString()
            prev.apply {
                setTextColor(if (idx != 0) Color.BLACK else Color.GRAY)
                isEnabled = idx != 0
            }
            next.apply {
                setTextColor(if (idx != items.lastIndex) Color.BLACK else Color.GRAY)
                isEnabled = idx != items.lastIndex
            }
            buy.apply {
                setTextColor(if (GameState.player.cash >= items[idx].value) Color.BLACK else Color.GRAY)
                isEnabled = GameState.player.cash >= items[idx].value
            }
        }
    }

    companion object IntentHandler {
        private const val SOME_STRING = "com.alec.mad.p2.someString"
        private const val IS_DEAD = "com.alec.mad.p2.isDead"

        fun getIntent(c: Context, someString: String) : Intent = Intent(c, ShopActivity::class.java).apply {
            putExtra("theNumberSeventeen", 17)
            putExtra(SOME_STRING, someString)
        }

        fun areTheyDead(intent: Intent) =
            intent.getBooleanExtra(IS_DEAD, false)

        private fun theyDied() = Intent().putExtra(IS_DEAD, true)
    }
}