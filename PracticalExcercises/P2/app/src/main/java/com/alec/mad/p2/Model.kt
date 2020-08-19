package com.alec.mad.p2

sealed class Item(val description: String, val value: Int)

class Food(description: String, value: Int, val health: Float) : Item(description, value)

class Equipment(description: String, value: Int, val mass: Float) : Item(description, value)

class Player(var xLoc: Int, var yLoc: Int, val cash: Int, var health: Float) {
    val equipment : MutableList<Equipment> = mutableListOf()
    val equipmentMass: Float
        get() = equipment.map { it.mass }.sum()
}

class Area(val type: AreaType, val items: List<Item> = mutableListOf()) {
    enum class AreaType(val vanityName: String) {
        TOWN("Town"),
        WILDERNESS("Wilderness")
    }
}

typealias GameMap = Array<Array<Area>>

operator fun GameMap.get(player: Player) = this[player.xLoc][player.yLoc]