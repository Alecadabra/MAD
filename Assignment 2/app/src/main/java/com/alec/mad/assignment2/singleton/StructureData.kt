package com.alec.mad.assignment2.singleton

import com.alec.mad.assignment2.R.drawable
import com.alec.mad.assignment2.model.ImageIDStructure
import com.alec.mad.assignment2.model.Structure
import com.alec.mad.assignment2.model.StructureType.*

/**
 * Wrapper singleton of a list of [Structure]s that are available to build.
 */
object StructureData : List<Structure> by structures

private val structures = listOf(
    RESIDENTIAL to drawable.ic_building1,
    RESIDENTIAL to drawable.ic_building2,
    RESIDENTIAL to drawable.ic_building3,
    RESIDENTIAL to drawable.ic_building4,
    COMMERCIAL to drawable.ic_building5,
    COMMERCIAL to drawable.ic_building6,
    COMMERCIAL to drawable.ic_building7,
    COMMERCIAL to drawable.ic_building8,
    ROAD to drawable.ic_road_e,
    ROAD to drawable.ic_road_ew,
    ROAD to drawable.ic_road_n,
    ROAD to drawable.ic_road_ne,
    ROAD to drawable.ic_road_new,
    ROAD to drawable.ic_road_ns,
    ROAD to drawable.ic_road_nse,
    ROAD to drawable.ic_road_nsew,
    ROAD to drawable.ic_road_nsw,
    ROAD to drawable.ic_road_nw,
    ROAD to drawable.ic_road_s,
    ROAD to drawable.ic_road_se,
    ROAD to drawable.ic_road_sew,
    ROAD to drawable.ic_road_sw,
    ROAD to drawable.ic_road_w
).map { ImageIDStructure(it.first, it.second) }


