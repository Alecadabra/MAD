package com.alec.mad.assignment1.fragment.selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.state.LayoutState
import com.alec.mad.assignment1.state.LayoutState.Orientation
import com.alec.mad.assignment1.state.LayoutStateObserver

/**
 * An abstracted fragment that holds primarily a recyclerview of derivations of SelectorCellModel.
 */
abstract class AbstractSelectorFragment<T : SelectorCellModel>(
    /** Whether this fragment should have a back button at the top */
    private val useBackButton: Boolean,
    /** Whether this fragment should show a layout changer at the top */
    private val useDynamicLayout: Boolean
) : Fragment(),
    LayoutStateObserver {

    companion object {
        // Instance state bundle keys
        private const val PACKAGE = "com.alec.mad.assignment1.fragment.selector"
        private const val BUNDLE_LAYOUT_CONTROLLER = "$PACKAGE.layoutController"
        private const val BUNDLE_RV_LAYOUT_MANAGER = "$PACKAGE.rvLayoutManager"
    }

    // View reference
    private lateinit var rv: RecyclerView

    /**
     * The state reference for the layout of the recycler view.
     * */
    private lateinit var layoutState: LayoutState

    /**
     * Values that populate the recycler view.
     * */
    protected abstract val values: List<T>

    /**
     * Text to show.
     * */
    protected abstract val title: String

    /**
     * Determines the layout ID to use in adapter based on the current orientation from the layout
     * state.
     */
    private val cellLayout: Int
        get() = when (this.layoutState.orientationEnum) {
            Orientation.HORIZONTAL -> R.layout.fragment_selector_cell_hor
            Orientation.VERTICAL -> R.layout.fragment_selector_cell_vert
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up layout controller from saved state or create new
        this.layoutState = savedInstanceState?.getParcelable(BUNDLE_LAYOUT_CONTROLLER)
            ?: LayoutState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // Get rv reference to use
        val localRv = if (this::rv.isInitialized) {
            // Get from state
            this.rv
        } else {
            // Find from view
            this.view?.findViewById(R.id.selectorList) as? RecyclerView
                ?: throw IllegalStateException("RecyclerView not present")
        }

        savedInstanceState?.also { bundle ->
            // Restore rv layout manager from instance state if non-null
            localRv.layoutManager?.onRestoreInstanceState(
                bundle.getParcelable(BUNDLE_RV_LAYOUT_MANAGER)
            )
        } ?: this.run {
            // If null saved instance state, set a new layout manager
            localRv.layoutManager = GridLayoutManager(
                this.context,
                this.layoutState.spanCount,
                this.layoutState.orientation,
                false
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_abstract_selector, container, false
        )

        // Set the title
        view.findViewById<TextView>(R.id.selectorTitle)?.text = this.title

        // Set up recycler view
        this.rv = view.findViewById(R.id.selectorList)
        if (rv.layoutManager == null) {
            rv.layoutManager = GridLayoutManager(
                this.context,
                this.layoutState.spanCount,
                this.layoutState.orientation,
                false
            )
        }
        rv.adapter = SelectorFragmentAdapter(this.values, this.cellLayout)

        // Handle dynamic layout
        if (this.useDynamicLayout) {
            // Add the layout changer to it's frame if not there
            val fragment = childFragmentManager.findFragmentById(R.id.selectorLayoutChangerFrame)
            if (fragment !is LayoutChangerFragment) {
                childFragmentManager.beginTransaction().also { transaction ->
                    transaction.add(
                        R.id.selectorLayoutChangerFrame,
                        LayoutChangerFragment(this.layoutState)
                    )

                    transaction.commit()
                }
            }

            // Give the layout controller access to this fragment
            this.layoutState.observers.add(this)
        }

        // Handle back button
        view.findViewById<ImageButton>(R.id.selectorBackButton).also { backButton ->
            backButton.isClickable = this.useBackButton
            backButton.isEnabled = this.useBackButton

            if (!this.useBackButton) {
                // Hide if not used
                backButton.setBackgroundResource(android.R.color.transparent)
                backButton.setImageResource(android.R.color.transparent)
                backButton.maxWidth = 0
            } else backButton.setOnClickListener {
                // Acts as the android back button
                fragmentManager?.popBackStack()
                    ?: throw IllegalStateException("Fragment manager not present")
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save layout controller state
        this.layoutState.observers.remove(this)
        outState.putParcelable(BUNDLE_LAYOUT_CONTROLLER, this.layoutState)

        // If late-init rv is initialised, save its state
        if (this::rv.isInitialized) {
            // Save recyclerview layout manager state if non null
            this.rv.layoutManager?.onSaveInstanceState().also { lm ->
                outState.putParcelable(BUNDLE_RV_LAYOUT_MANAGER, lm)
            }
        }
    }

    // Observer of layout changer
    @CallSuper
    override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
        // Change the layout of the recyclerview
        val lm = this.rv.layoutManager as? GridLayoutManager
            ?: throw IllegalStateException("RecyclerView layout manager null")
        lm.orientation = orientation

        // Change the adapter to use the new orientation cell layout
        val rv = this.view?.findViewById(R.id.selectorList) as? RecyclerView
            ?: throw IllegalStateException("RecyclerView not present")
        rv.adapter = SelectorFragmentAdapter(this.values, this.cellLayout)
    }

    // Observer of layout changer
    @CallSuper
    override fun onUpdateSpanCount(spanCount: Int) {
        // Change the span count of the recyclerview
        val lm = this.rv.layoutManager as? GridLayoutManager
            ?: throw IllegalStateException("RecyclerView layout manager null")
        lm.spanCount = spanCount
    }

    // For subclasses to include their own implementation in */
    @CallSuper
    open fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: T) {
        holder.text.text = item.text
        holder.imageButton.setImageResource(item.imageSrc)
        holder.imageButton.setBackgroundColor(item.bgColor)
        holder.text.setTextColor(item.textColor)
    }

    // Recycler view adapter
    inner class SelectorFragmentAdapter(
        private val values: List<T>,
        private val cellLayout: Int // The cell layout ID to use (Horizontal or vertical)
    ) : RecyclerView.Adapter<SelectorFragmentAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(this.cellLayout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            this@AbstractSelectorFragment.bindViewHolder(holder, item)
        }

        override fun getItemCount(): Int = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageButton: ImageButton = view.findViewById(R.id.fragmentSelectorCellImage)
            val text: TextView = view.findViewById(R.id.fragmentSelectorCellText)
        }
    }
}