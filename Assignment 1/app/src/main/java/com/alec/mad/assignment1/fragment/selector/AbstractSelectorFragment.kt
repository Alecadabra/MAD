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
import com.alec.mad.assignment1.fragment.LayoutChangerFragment
import com.alec.mad.assignment1.LayoutController
import com.alec.mad.assignment1.LayoutControllerObserver
import com.alec.mad.assignment1.Orientation

abstract class AbstractSelectorFragment<T : SelectorCellModel> : Fragment(),
    LayoutControllerObserver {

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment1.fragment.selector"
        private const val BUNDLE_LAYOUT_CONTROLLER = "$PACKAGE.layoutController"
        private const val BUNDLE_RV_LAYOUT_MANAGER = "$PACKAGE.rvLayoutManager"
    }

    private lateinit var layoutController: LayoutController

    protected abstract val values: List<T>

    protected abstract val title: String

    private val layoutManager: GridLayoutManager
        get() {
            val rv = this.view?.findViewById(R.id.selectorList) as? RecyclerView
                ?: throw IllegalStateException("RecyclerView not present")
            return rv.layoutManager as? GridLayoutManager
                ?: throw IllegalStateException("GridLayoutManager not present")
        }

    /**
     * Layout ID to use in adapter
     */
    private val cellLayout: Int
        get() = when (layoutController.orientationEnum) {
            Orientation.HORIZONTAL -> R.layout.fragment_selector_cell_hor
            Orientation.VERTICAL -> R.layout.fragment_selector_cell_vert
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up layout controller
        this.layoutController = savedInstanceState?.getParcelable(BUNDLE_LAYOUT_CONTROLLER)
            ?: LayoutController()/* TODO Debug print */.also { println("Couldn't get parcel for ${this.values[0]}") }
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
        val rv = view.findViewById(R.id.selectorList) as RecyclerView
        // Get layout manager from saved state or create new
        rv.layoutManager = savedInstanceState?.getParcelable(BUNDLE_RV_LAYOUT_MANAGER)
            ?: GridLayoutManager(
                this.context,
                this.layoutController.spanCount,
                this.layoutController.orientation,
                false
            )
        rv.adapter = SelectorFragmentAdapter(this.values, this.cellLayout).also {
            println("Just set the adapter to ${layoutController.orientationEnum.name} for values ${this.values[0]}")
        }

        // Add the layout changer to it's frame if not there
        childFragmentManager.findFragmentById(R.id.selectorLayoutChangerFrame) as? LayoutChangerFragment
            ?: childFragmentManager.beginTransaction().also { transaction ->
                transaction.add(
                    R.id.selectorLayoutChangerFrame,
                    LayoutChangerFragment(this.layoutController)
                )

                transaction.commit()
            }

        // Give the layout controller access to this fragment
        this.layoutController.observers.add(this)

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // TODO Debug print
        println("Selector for ${this.values[0]} is saving state")

        // Save layout controller state
        this.layoutController.observers.remove(this)
        outState.putParcelable(BUNDLE_LAYOUT_CONTROLLER, this.layoutController)

        // Save recyclerview state
        this.layoutManager.onSaveInstanceState()?.also { lm ->
            outState.putParcelable(BUNDLE_RV_LAYOUT_MANAGER, lm)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        println("Selector for ${this.values[0]} is having view destroyed")
    }

    @CallSuper
    override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
        // Change the layout of the recyclerview
        this.layoutManager.orientation = orientation

        // Change the adapter to use the new orientation cell layout
        val rv = this.view?.findViewById(R.id.selectorList) as? RecyclerView
            ?: throw IllegalStateException("RecyclerView not present")
        rv.adapter = SelectorFragmentAdapter(this.values, this.cellLayout)
    }

    @CallSuper
    override fun onUpdateSpanCount(spanCount: Int) {
        // Change the span count of the recyclerview
        this.layoutManager.spanCount = spanCount
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
}