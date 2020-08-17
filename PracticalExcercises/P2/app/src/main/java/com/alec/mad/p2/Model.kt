package com.alec.mad.p2

sealed class Item(val description: String, val value: Int)

class Food(description: String, value: Int, val health: Float) : Item(description, value)

class Equipment(description: String, value: Int, val mass: Float) : Item(description, value)

class Player(val location: Location, val cash: Int, val health: Float) {
    val equipment : MutableList<Equipment> = mutableListOf()
    val equipmentMass: Float
        get() = equipment.map { it.mass }.sum()

    data class Location(var x: Int, var y: Int)
}

class Area(val type: AreaType, val items: List<Item> = mutableListOf()) {
    enum class AreaType(val vanityName: String) {
        TOWN("Town"),
        WILDERNESS("Wilderness")
    }
}

class GameMap(val grid: Array<Array<Area>>) {
    operator fun get(location: Player.Location): Area {
        return grid[location.x][location.y]
    }
}