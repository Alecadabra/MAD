package com.alec.mad.assignment1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.dummy.DummyContent
import com.alec.mad.assignment1.singleton.LayoutController
import com.alec.mad.assignment1.singleton.LayoutControllerObserver

open class AbstractSelectorFragment : Fragment(), LayoutControllerObserver {

    private val layoutManager: GridLayoutManager
        get() {
            val recyclerView = this.view as? RecyclerView
                ?: throw IllegalStateException("RecyclerView not present")
            return recyclerView.layoutManager as? GridLayoutManager
                ?: throw IllegalStateException("GridLayoutManager not present")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_abstract_selector_recyclerview, container, false
        )

        // Give the layout controller access to this fragment
        LayoutController.observers.add(this)

        // Set the adapter
        if (view is RecyclerView) {
            /*view.layoutManager = when {
                spanCount <= 1 -> LinearLayoutManager(this.context, this.orientation, false)
                else -> GridLayoutManager(this.context, LayoutController.spanCount, LayoutController.orientation, false)
            }*/
            view.layoutManager = GridLayoutManager(
                this.context,
                LayoutController.spanCount,
                LayoutController.orientation,
                false
            )
            view.adapter = AbstractSelectorFragmentAdapter(DummyContent.ITEMS)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        LayoutController.observers.remove(this)
    }

    @CallSuper
    override fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {
        this.layoutManager.orientation = orientation
    }

    @CallSuper
    override fun onUpdateSpanCount(spanCount: Int) {
        this.layoutManager.spanCount = spanCount
    }

    class AbstractSelectorFragmentAdapter(
        private val values: List<DummyContent.DummyItem>
    ) : RecyclerView.Adapter<AbstractSelectorFragmentAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_selector_cell, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.content
        }

        override fun getItemCount(): Int = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.item_number)
            val contentView: TextView = view.findViewById(R.id.content)

            override fun toString(): String = "${super.toString()} '${contentView.text}'"

        }
    }
}