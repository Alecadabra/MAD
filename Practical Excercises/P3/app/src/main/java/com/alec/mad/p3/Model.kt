package com.alec.mad.p3

/**
 * Represents a single grid square in the map. Each map element has both terrain and an optional
 * structure.
 *
 * The terrain comes in four pieces, as if each grid square was further divided into its own tiny
 * 2x2 grid (north-west, north-east, south-west and south-east). Each piece of the terrain is
 * represented as an int, which is actually a drawable reference. That is, if you have both an
 * ImageView and a MapElement, you can do this:
 *
 * ImageView iv = ...;
 * MapElement me = ...;
 * iv.setImageResource(me.getNorthWest());
 *
 * This will cause the ImageView to display the grid square's north-western terrain image,
 * whatever it is.
 *
 * (The terrain is broken up like this because there are a lot of possible combinations of terrain
 * images for each grid square. If we had a single terrain image for each square, we'd need to
 * manually combine all the possible combinations of images, and we'd get a small explosion of
 * image files.)
 *
 * Meanwhile, the structure is something we want to display over the top of the terrain. Each
 * MapElement has either zero or one Structure} objects. For each grid square, we can also change
 * which structure is built on it.
 */
class MapElement(
    val isBuildable: Boolean,
    val northWest: Int,
    val northEast: Int,
    val southWest: Int,
    val southEast: Int,
    /**
     * Retrieves the structure built on this map element.
     * @return The structure, or null if one is not present.
     */
    var structure: Structure?
)

/**
 * Represents a possible structure to be placed on the map. A structure simply contains a drawable
 * int reference, and a string label to be shown in the selector.
 */
class Structure(val drawableId: Int, val label: String)