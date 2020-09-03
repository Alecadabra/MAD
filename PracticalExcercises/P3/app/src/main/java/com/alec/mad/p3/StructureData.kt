import com.alec.mad.p3.R
import java.util.*

/**
 * Stores the list of possible structures. This has a static get() method for retrieving an
 * instance, rather than calling the constructor directly.
 *
 * The remaining methods -- get(int), size(), add(Structure) and remove(int) -- provide
 * minimalistic list functionality.
 *
 * There is a static int array called DRAWABLES, which stores all the drawable integer references,
 * some of which are not actually used (yet) in a Structure object.
 */
object StructureData {
    val structureList = mutableListOf(
        Structure(R.drawable.ic_building1, "House"),
        Structure(R.drawable.ic_building2, "House"),
        Structure(R.drawable.ic_road_ns, "Road"),
        Structure(R.drawable.ic_road_ew, "Road"),
        Structure(R.drawable.ic_road_nsew, "Road"),
        Structure(R.drawable.ic_road_ne, "Road"),
        Structure(R.drawable.ic_road_nw, "Road"),
        Structure(R.drawable.ic_road_se, "Road"),
        Structure(R.drawable.ic_road_sw, "Road"),
        Structure(R.drawable.ic_road_n, "Road"),
        Structure(R.drawable.ic_road_e, "Road"),
        Structure(R.drawable.ic_road_s, "Road"),
        Structure(R.drawable.ic_road_w, "Road"),
        Structure(R.drawable.ic_road_nse, "Road"),
        Structure(R.drawable.ic_road_nsw, "Road"),
        Structure(R.drawable.ic_road_new, "Road"),
        Structure(R.drawable.ic_road_sew, "Road"),
        Structure(R.drawable.ic_tree1, "Tree"),
        Structure(R.drawable.ic_tree2, "Tree"),
        Structure(R.drawable.ic_tree3, "Tree"),
        Structure(R.drawable.ic_tree4, "Tree")
    )

    val size: Int
        get() {
            return structureList.size
        }

    operator fun get(i: Int): Structure {
        return structureList[i]
    }

    fun add(s: Structure) {
        structureList.add(0, s)
    }

    fun remove(i: Int) {
        structureList.removeAt(i)
    }

    val DRAWABLES = intArrayOf(
        0,  // No structure
        R.drawable.ic_building1,
        R.drawable.ic_building2,
        R.drawable.ic_building3,
        R.drawable.ic_building4,
        R.drawable.ic_building5,
        R.drawable.ic_building6,
        R.drawable.ic_building7,
        R.drawable.ic_building8,
        R.drawable.ic_road_ns,
        R.drawable.ic_road_ew,
        R.drawable.ic_road_nsew,
        R.drawable.ic_road_ne,
        R.drawable.ic_road_nw,
        R.drawable.ic_road_se,
        R.drawable.ic_road_sw,
        R.drawable.ic_road_n,
        R.drawable.ic_road_e,
        R.drawable.ic_road_s,
        R.drawable.ic_road_w,
        R.drawable.ic_road_nse,
        R.drawable.ic_road_nsw,
        R.drawable.ic_road_new,
        R.drawable.ic_road_sew,
        R.drawable.ic_tree1,
        R.drawable.ic_tree2,
        R.drawable.ic_tree3,
        R.drawable.ic_tree4
    )
}