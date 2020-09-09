package com.alec.mad.p3

import java.util.*

/**
 * Represents the overall map, and contains a grid of MapElement objects (accessible using the
 * get(row, col) method). The two static constants WIDTH and HEIGHT indicate the size of the map.
 *
 * There is a static get() method to be used to obtain an instance (rather than calling the
 * constructor directly).
 *
 * There is also a regenerate() method. The map is randomly-generated, and this method will invoke
 * the algorithm again to replace all the map data with a new randomly-generated grid.
 */
class MapData(var grid: Array<Array<MapElement?>>) {

    fun regenerate() {
        grid = generateGrid()
    }

    operator fun get(i: Int, j: Int): MapElement? {
        return grid[i][j]
    }

    companion object {
        const val WIDTH = 30
        const val HEIGHT = 10
        private val WATER: Int = R.drawable.ic_water
        private val GRASS = intArrayOf(
            R.drawable.ic_grass1, R.drawable.ic_grass2,
            R.drawable.ic_grass3, R.drawable.ic_grass4
        )
        private val rng = Random()
        private var _instance: MapData? = null
        val instance: MapData
            get() {
                if (_instance == null) {
                    _instance = MapData(generateGrid())
                }
                return _instance
                    ?: throw AssertionError("Backing field set to null by another thread")
            }

        private fun generateGrid(): Array<Array<MapElement?>> {
            val HEIGHT_RANGE = 256
            val WATER_LEVEL = 112
            val INLAND_BIAS = 24
            val AREA_SIZE = 1
            val SMOOTHING_ITERATIONS = 2
            var heightField = Array(HEIGHT) { IntArray(WIDTH) }
            for (i in 0 until HEIGHT) {
                for (j in 0 until WIDTH) {
                    heightField[i][j] = (rng.nextInt(HEIGHT_RANGE)
                            + INLAND_BIAS * (i.coerceAtMost(j)
                        .coerceAtMost((HEIGHT - i - 1).coerceAtMost(WIDTH - j - 1)) -
                            HEIGHT.coerceAtMost(WIDTH) / 4))
                }
            }
            var newHf = Array(HEIGHT) { IntArray(WIDTH) }
            for (s in 0 until SMOOTHING_ITERATIONS) {
                for (i in 0 until HEIGHT) {
                    for (j in 0 until WIDTH) {
                        var areaSize = 0
                        var heightSum = 0
                        for (areaI in 0.coerceAtLeast(i - AREA_SIZE) until HEIGHT.coerceAtMost(i + AREA_SIZE + 1)) {
                            for (areaJ in 0.coerceAtLeast(j - AREA_SIZE) until WIDTH.coerceAtMost(j + AREA_SIZE + 1)) {
                                areaSize++
                                heightSum += heightField[areaI][areaJ]
                            }
                        }
                        newHf[i][j] = heightSum / areaSize
                    }
                }
                val tmpHf = heightField
                heightField = newHf
                newHf = tmpHf
            }
            val grid =
                Array(HEIGHT) { arrayOfNulls<MapElement>(WIDTH) }
            for (i in 0 until HEIGHT) {
                for (j in 0 until WIDTH) {
                    var element: MapElement
                    if (heightField[i][j] >= WATER_LEVEL) {
                        val waterN = i == 0 || heightField[i - 1][j] < WATER_LEVEL
                        val waterE =
                            j == WIDTH - 1 || heightField[i][j + 1] < WATER_LEVEL
                        val waterS =
                            i == HEIGHT - 1 || heightField[i + 1][j] < WATER_LEVEL
                        val waterW = j == 0 || heightField[i][j - 1] < WATER_LEVEL
                        val waterNW = i == 0 || j == 0 || heightField[i - 1][j - 1] < WATER_LEVEL
                        val waterNE =
                            i == 0 || j == WIDTH - 1 || heightField[i - 1][j + 1] < WATER_LEVEL
                        val waterSW =
                            i == HEIGHT - 1 || j == 0 || heightField[i + 1][j - 1] < WATER_LEVEL
                        val waterSE =
                            i == HEIGHT - 1 || j == WIDTH - 1 || heightField[i + 1][j + 1] < WATER_LEVEL
                        val coast = waterN || waterE || waterS || waterW ||
                                waterNW || waterNE || waterSW || waterSE
                        grid[i][j] = MapElement(
                            !coast,
                            choose(
                                waterN, waterW, waterNW,
                                R.drawable.ic_coast_north, R.drawable.ic_coast_west,
                                R.drawable.ic_coast_northwest, R.drawable.ic_coast_northwest_concave
                            ),
                            choose(
                                waterN, waterE, waterNE,
                                R.drawable.ic_coast_north, R.drawable.ic_coast_east,
                                R.drawable.ic_coast_northeast, R.drawable.ic_coast_northeast_concave
                            ),
                            choose(
                                waterS, waterW, waterSW,
                                R.drawable.ic_coast_south, R.drawable.ic_coast_west,
                                R.drawable.ic_coast_southwest, R.drawable.ic_coast_southwest_concave
                            ),
                            choose(
                                waterS, waterE, waterSE,
                                R.drawable.ic_coast_south, R.drawable.ic_coast_east,
                                R.drawable.ic_coast_southeast, R.drawable.ic_coast_southeast_concave
                            ),
                            null
                        )
                    } else {
                        grid[i][j] = MapElement(
                            false,
                            WATER,
                            WATER,
                            WATER,
                            WATER,
                            null
                        )
                    }
                }
            }
            return grid
        }

        private fun choose(
            nsWater: Boolean, ewWater: Boolean, diagWater: Boolean,
            nsCoastId: Int, ewCoastId: Int, convexCoastId: Int, concaveCoastId: Int
        ): Int {
            return if (nsWater) {
                if (ewWater) {
                    convexCoastId
                } else {
                    nsCoastId
                }
            } else {
                if (ewWater) {
                    ewCoastId
                } else if (diagWater) {
                    concaveCoastId
                } else {
                    GRASS[rng.nextInt(GRASS.size)]
                }
            }
        }
    }
}