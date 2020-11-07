package com.alec.mad.assignment2.controller

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.core.util.rangeTo
import com.alec.mad.assignment2.model.Structure
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.model.Settings
import com.alec.mad.assignment2.singleton.State

class BuildIntent(private val context: Context?, private val structure: Structure) {

    fun buildAt(i: Int, j: Int): Boolean {
        val success: Boolean
        val structureType = this.structure.structureType
        val cost = when (structureType) {
            StructureType.RESIDENTIAL -> State.gameData.settings.houseBuildCost
            StructureType.COMMERCIAL -> State.gameData.settings.commBuildCost
            StructureType.ROAD -> State.gameData.settings.roadBuildCost
        }

        if (gameData.map[i, j].structure != null) {
            // Already a structure there
            toast("There is already a building there!")
            success = false

        } else if (gameData.money < cost) {
            // Can't afford
            toast("You can not afford to build this")
            success = false

        } else {
            val roadAdjacent = setOf(i + 1 to j, i - 1 to j, i to j + 1, i to j - 1).map {
                checkForRoadAt(it.first, it.second)
            }.reduce(Boolean::or)

            if (structureType != StructureType.ROAD && !roadAdjacent) {
                // Not a valid spot to build
                toast("You can only build this next to a road")
                success = false

            } else {
                // All checks passed
                map[i, j].structure = this.structure
                if (this.structure.structureType == StructureType.RESIDENTIAL) {
                    gameData.numResidential++
                } else if (this.structure.structureType == StructureType.COMMERCIAL) {
                    gameData.numCommercial++
                }
                gameData.money -= cost
                success = true
            }
        }
        return success
    }

    private fun checkForRoadAt(i: Int, j: Int): Boolean {
        // Check bounds
        return if (i in 0 .. map.lastRowIndex && j in 0 .. map.lastColIndex) {
            map[i, j].structure?.let { nullSafeStructure ->
                nullSafeStructure.structureType == StructureType.ROAD
            } ?: false
        } else false
    }

    private fun toast(message: String, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(
        this.context, message, length
    ).also { it.setGravity(Gravity.CENTER, 0, 0) }.show()

    companion object {
        private val gameData = State.gameData
        private val map = gameData.map
    }
}