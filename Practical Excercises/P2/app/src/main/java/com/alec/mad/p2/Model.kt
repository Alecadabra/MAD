package com.alec.mad.p2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Item(val description: String, val value: Int)

class Food(description: String, value: Int, val health: Float) : Item(description, value) {
    override fun toString(): String = """
        |$description ($value gold)
        |Provides $health health
        |""".trimMargin()
}

class Equipment(description: String, value: Int, val mass: Float) : Item(description, value) {
    override fun toString(): String = """
        |$description ($value gold)
        |Weighs ${mass}kg
        |""".trimMargin()
}

class Player(var xLoc: Int, var yLoc: Int, var cash: Int, var health: Float) {
    val equipment : MutableList<Equipment> = mutableListOf()
    val equipmentMass: Float
        get() = equipment.map { it.mass }.sum()
}

class Area(val type: AreaType, val items: MutableList<Item> = mutableListOf()) {
    enum class AreaType(val vanityName: String) {
        TOWN("Town"),
        WILDERNESS("Wilderness")
    }
}

typealias GameMap = Array<Array<Area>>

operator fun GameMap.get(player: Player) = this[player.xLoc][player.yLoc]