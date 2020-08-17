package com.alec.mad.p2

import kotlin.random.Random

object GameState {
    private const val STARTING_HEALTH = 100F
    private const val STARTING_CASH = 50
    private const val GAME_MAP_SIZE_X = 10
    private const val GAME_MAP_SIZE_Y = 10
    private const val AREA_ITEM_LIST_MIN = 2
    private const val AREA_ITEM_LIST_MAX = 8
    private val STARTING_LOCATION = Player.Location(4, 4)

    var gameMap: GameMap = newGameMap()
    var player: Player = newPlayer()

    /**
     * Resets the game state
     */
    fun resetGame() {
        gameMap = newGameMap()
        player = newPlayer()
    }

    /**
     * Creates a new player with starting location, cash and health.
     */
    private fun newPlayer(): Player {
        return Player(STARTING_LOCATION, STARTING_CASH, STARTING_HEALTH)
    }

    /**
     * Creates a new game map of dimensions GAME_MAP_SIZE_X by GAME_MAP_SIZE_Y with randomly
     * generated Area tiles.
     */
    private fun newGameMap(): GameMap {
        return GameMap(Array(GAME_MAP_SIZE_X) { Array(GAME_MAP_SIZE_Y)
        {
            Area(
                Area.AreaType.values()[Random.nextInt(0, Area.AreaType.values().size)],
                newItemList()
            )
        }} )
    }

    /**
     * Creates a new list of Items of size between AREA_ITEM_LIST_MIN and AREA_ITEM_LIST_MAX and
     * initialises with items from ItemLibrary.
     */
    private fun newItemList(): List<Item> {
        return List(Random.nextInt(AREA_ITEM_LIST_MIN, AREA_ITEM_LIST_MAX)) {
            ItemLibrary.randomItem
        }
    }

    object ItemLibrary {
        val randomItem: Item
            get() = items[Random.nextInt(0, items.size)]

        private val items: List<Item> = listOf(
            Food("Apple", 3, 5F),
            Food("Vegemite scroll", 6, 8F),
            Food("Pasta bake", 10, 10F),
            Food("Pumpkin Pie", 12, 18F),
            Equipment("Small stone", 1, 1F),
            Equipment("Old Clothes", 3, 1F),
            Equipment("Lump of gold", 20, 25F)
        )
    }
}