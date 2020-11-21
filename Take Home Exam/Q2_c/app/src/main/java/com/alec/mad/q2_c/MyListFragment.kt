package com.alec.mad.q2_c

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyListFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
                R.layout.fragment_item_list,
                container,
                false
        ) as? RecyclerView ?: error("View not recycler view")

        // Set RV params
        view.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        )
        view.adapter = MyListAdapter()

        return view
    }

    class MyListAdapter
        : RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): MyListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.fragment_item, parent, false
            )
            return MyListViewHolder(view)
        }

        override fun onBindViewHolder(
                holder: MyListViewHolder,
                position: Int
        ) {
            // Set text in the view holder
            @SuppressLint("SetTextI18n")
            val text = "Button ${position + 1}"
            holder.textView.text = text
            holder.textView.setOnClickListener { Log.i("Button", text) }
        }

        // Show 100 items
        override fun getItemCount(): Int = 100

        inner class MyListViewHolder(
                view: View
        ) : RecyclerView.ViewHolder(view) {
            // Text field
            val textView: TextView = view.findViewById(R.id.textView)
        }
    }
}