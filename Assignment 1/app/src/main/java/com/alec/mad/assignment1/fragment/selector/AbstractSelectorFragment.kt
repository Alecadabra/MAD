package com.alec.mad.assignment1.fragment.selector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.LayoutChangerFragment
import com.alec.mad.assignment1.state.LayoutState
import com.alec.mad.assignment1.state.LayoutState.Orientation
import com.alec.mad.assignment1.state.LayoutStateObserver


abstract class AbstractSelectorFragment<T : SelectorCellModel>(
    val useBackButton: Boolean, val useDynamicLayout: Boolean
) : Fragment(),
    LayoutStateObserver {

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment1.fragment.selector"
        private const val BUNDLE_LAYOUT_CONTROLLER = "$PACKAGE.layoutController"
        private const val BUNDLE_RV_LAYOUT_MANAGER = "$PACKAGE.rvLayoutManager"
    }

    // View reference
    private lateinit var rv: RecyclerView

    private lateinit var layoutState: LayoutState

    protected abstract val values: List<T>

    protected abstract val title: String

    /**
     * Layout ID to use in adapter
     */
    private val cellLayout: Int
        get() = when (this.layoutState.orientationEnum) {
            Orientation.HORIZONTAL -> R.layout.fragment_selector_cell_hor
            Orientation.VERTICAL -> R.layout.fragment_selector_cell_vert
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up layout controller
        this.layoutState = savedInstanceState?.getParcelable(BUNDLE_LAYOUT_CONTROLLER)
            ?: LayoutState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val rv = this.view?.findViewById<RecyclerView>(R.id.selectorList)
            ?: throw IllegalStateException("RecyclerView not present")

        savedInstanceState?.also { bundle ->
            // Restore from instance state
            rv.layoutManager?.onRestoreInstanceState(bundle.getParcelable(BUNDLE_RV_LAYOUT_MANAGER))
        } ?: this.run {
            // If no saved instance state, set a new layout manager
            rv.layoutManager = GridLayoutManager(
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
                backButton.setBackgroundResource(android.R.color.transparent)
                backButton.setImageResource(android.R.color.transparent)
                backButton.maxWidth = 0
            } else backButton.setOnClickListener {
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

        // Save recyclerview layout manager state if non null
        this.rv.layoutManager?.onSaveInstanceState().also { lm ->
            outState.putParcelable(BUNDLE_RV_LAYOUT_MANAGER, lm)
        }
    }

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

    @CallSuper
    override fun onUpdateSpanCount(spanCount: Int) {
        // Change the span count of the recyclerview
        val lm = this.rv.layoutManager as? GridLayoutManager
            ?: throw IllegalStateException("RecyclerView layout manager null")
        lm.spanCount = spanCount
    }

    @CallSuper
    open fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: T) {
        holder.imageButton.setImageResource(item.imageSrc)
        holder.attr1.text = item.attr1
        holder.attr2.text = item.attr2
        holder.attr3.text = item.attr3
    }

    inner class SelectorFragmentAdapter(
        private val values: List<T>, private val cellLayout: Int
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
            val attr1: TextView = view.findViewById(R.id.fragmentSelectorCellAttr1)
            val attr2: TextView = view.findViewById(R.id.fragmentSelectorCellAttr2)
            val attr3: TextView = view.findViewById(R.id.fragmentSelectorCellAttr3)
        }
    }

    /*
    @SuppressLint("SetTextI18n")
    inner class LayoutChangerFragment : Fragment(), LayoutStateObserver {

        // Views
        private lateinit var oneSpanBtn: Button
        private lateinit var twoSpanBtn: Button
        private lateinit var threeSpanBtn: Button
        private lateinit var orientationBtn: Button

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_layout_changer, container, false)

            // Get references to views
            this.oneSpanBtn = view.findViewById(R.id.oneSpanButton) as Button
            this.twoSpanBtn = view.findViewById(R.id.twoSpanButton) as Button
            this.threeSpanBtn = view.findViewById(R.id.threeSpanButton) as Button
            this.orientationBtn = view.findViewById(R.id.swapOrientationButton) as Button

            // Set button listeners
            this.oneSpanBtn.setOnClickListener {
                this@AbstractSelectorFragment.layoutState.spanCount = 1
            }
            this.twoSpanBtn.setOnClickListener {
                this@AbstractSelectorFragment.layoutState.spanCount = 2
            }
            this.threeSpanBtn.setOnClickListener {
                this@AbstractSelectorFragment.layoutState.spanCount = 3
            }
            this.orientationBtn.setOnClickListener {
                this@AbstractSelectorFragment.layoutState.swapOrientation()
            }

            // Set layout controller observer and manually trigger once
            this@AbstractSelectorFragment.layoutState.observers.add(this)
            this.onUpdateOrientation(this@AbstractSelectorFragment.layoutState.orientation)
            this.onUpdateSpanCount(this@AbstractSelectorFragment.layoutState.spanCount)

            return view
        }

        override fun onDestroyView() {
            super.onDestroyView()
            this@AbstractSelectorFragment.layoutState.observers.remove(this)
        }

        override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
            when (orientation) {
                LayoutState.HORIZONTAL -> {
                    this.oneSpanBtn.text = "1 Row"
                    this.twoSpanBtn.text = "2 Rows"
                    this.threeSpanBtn.text = "3 Rows"
                    this.orientationBtn.text = "Cols"
                }
                LayoutState.VERTICAL -> {
                    this.oneSpanBtn.text = "1 Col"
                    this.twoSpanBtn.text = "2 Cols"
                    this.threeSpanBtn.text = "3 Cols"
                    this.orientationBtn.text = "Rows"
                }
            }
        }
    }

     */
}