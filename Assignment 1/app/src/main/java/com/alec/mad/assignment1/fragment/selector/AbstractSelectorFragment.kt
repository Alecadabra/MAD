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
import com.alec.mad.assignment1.singleton.LayoutController
import com.alec.mad.assignment1.singleton.LayoutControllerObserver
import com.alec.mad.assignment1.singleton.Orientation

abstract class AbstractSelectorFragment<T : SelectorCellModel> : Fragment(),
    LayoutControllerObserver {

    companion object {
        private const val BUNDLE_LAYOUT_CONTROLLER =
            "com.alec.mad.assignment1.fragment.selector.layoutController"
    }

    private lateinit var layoutController: LayoutController

    private val layoutManager: GridLayoutManager
        get() {
            val rv = this.view?.findViewById(R.id.selectorList) as? RecyclerView
                ?: throw IllegalStateException("RecyclerView not present")
            return rv.layoutManager as? GridLayoutManager
                ?: throw IllegalStateException("GridLayoutManager not present")
        }

    protected abstract val values: List<T>

    /**
     * Layout ID to use in adapter
     */
    private val cellLayout: Int
        get() = when (layoutController.orientationEnum) {
            Orientation.HORIZONTAL -> R.layout.fragment_selector_cell_hor
            Orientation.VERTICAL -> R.layout.fragment_selector_cell_vert
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_abstract_selector, container, false
        )

        // Set up layout controller
        this.layoutController = savedInstanceState?.getParcelable(BUNDLE_LAYOUT_CONTROLLER)
            ?: LayoutController()

        // Set up recycler view
        val rv = view.findViewById(R.id.selectorList) as RecyclerView
        rv.layoutManager = GridLayoutManager(
            this.context,
            this.layoutController.spanCount,
            this.layoutController.orientation,
            false
        )
        rv.adapter = SelectorFragmentAdapter(this.values, this.cellLayout)

        // TODO for some reason when coming back to this fragment, the layout changer is not null even though it isn't present in the frame when the view is inflated.
        // Add the layout changer to it's frame
        this.fragmentManager?.also { fm ->
            if (fm.findFragmentById(R.id.selectorLayoutChangerFrame) == null) {
                var transaction = fm.beginTransaction()
                val frag = LayoutChangerFragment(this.layoutController)

                // Add the starting screen to the game frame
                transaction = transaction.add(
                    R.id.selectorLayoutChangerFrame,
                    frag
                )

                transaction.commit()
            } else println("The fragment is there")
        } ?: throw IllegalStateException("Fragment manager null in create view")

        // Give the layout controller access to this fragment
        this.layoutController.observers.add(this)

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        this.layoutController.observers.remove(this)
        outState.putParcelable(BUNDLE_LAYOUT_CONTROLLER, this.layoutController)
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
            val attr3: TextView = view.findViewById(R.id.fragmentSelectorCellAttr2)
        }
    }
}