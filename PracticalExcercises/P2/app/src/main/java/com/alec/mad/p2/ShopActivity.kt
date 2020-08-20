package com.alec.mad.p2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_shop.*
import kotlin.properties.Delegates


class ShopActivity : AppCompatActivity() {

    private val items: MutableList<Item> = GameState.gameMap[GameState.player].items
    private var idx: Int by Delegates.observable(0) { _, _, _->
        @SuppressLint("SetTextI18n")
        counter.text = "Item ${idx + 1} of ${items.size}"
        description.text = items[idx].toString()
        error.text = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        idx = 0

        prev.setOnClickListener { idx = 0.coerceAtLeast(idx - 1) }
        next.setOnClickListener { idx = (idx + 1).coerceAtMost(items.lastIndex) }

        buy.setOnClickListener {
            val player = GameState.player
            when {
                player.cash >= items[idx].value -> {
                    // Remove cash from player
                    player.cash = 0.coerceAtLeast(player.cash - items[idx].value)
                    // Remove item from shop
                    items.remove(items[idx])
                    if (items.size == 0) {
                        @SuppressLint("SetTextI18n")
                        counter.text = "No items left"
                        description.text = ""
                        error.text = ""
                        prev.setOnClickListener {  }
                        next.setOnClickListener {  }
                        buy.setOnClickListener {  }
                    }
                    else {
                        // Adjust shop index
                        idx = 0.coerceAtLeast(idx - 1)
                    }
                }
                else -> {
                    @SuppressLint("SetTextI18n")
                    error.text = "Cannot afford!"
                }
        } }
    }

    companion object IntentHandler {
        fun getIntent(c: Context) : Intent = Intent(c, ShopActivity::class.java)
    }
}