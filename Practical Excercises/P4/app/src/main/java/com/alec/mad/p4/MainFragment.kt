package com.alec.mad.p4

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alec.mad.p4.Faction.Relationship

class MainFragment : Fragment() {

    /** Faction data */
    private lateinit var factionList: FactionList

    /** Views */
    private lateinit var views: ViewContainer

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialise the faction list when we get a non-null context
        this.factionList = FactionList(context).also {
            it.load()
        }
    }

    override fun onCreateView(li: LayoutInflater, parent: ViewGroup?, b: Bundle?): View {
        // Inflate the view
        val view: View = li.inflate(R.layout.fragment_main, parent, false)

        // Get all view references
        this.views = ViewContainer(
            nameSpinner = view.findViewById<View>(R.id.factionNameSpinner) as Spinner,
            nameEditor = view.findViewById<View>(R.id.factionNameEditor) as EditText,
            strength = view.findViewById<View>(R.id.strength) as EditText,
            relationship = view.findViewById<View>(R.id.relationship) as Spinner,
            addButton = view.findViewById<View>(R.id.addButton) as Button,
            rv = view.findViewById<View>(R.id.list) as RecyclerView,
            adapter = FactionAdapter(),
            rvLayout = LinearLayoutManager(activity)
        )

        // Set the initial state of the 'New Faction' part of the UI.
        this.views.relationship.setSelection(Relationship.DEFAULT.idx)
        this.views.strength.setText(Faction.DEFAULT_STRENGTH.toString())
        this.views.nameSpinner.setSelection(0)
        this.views.nameEditor.visibility = View.VISIBLE

        this.views.rv.adapter = this.views.adapter
        this.views.rv.layoutManager = this.views.rvLayout

        // We have two UI elements: a drop-down list (Spinner) and an editor (EditText). We want to
        // hide the editor *unless* the spinner's position is zero ("Custom..."). This event handler
        // arranges the the necessary state changes.
        this.views.nameSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?, view: View, position: Int, rowId: Long
            ) {
                this@MainFragment.views.nameEditor.visibility =
                    (if (position == 0) View.VISIBLE else View.GONE)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                this@MainFragment.views.nameSpinner.setSelection(0)
            }
        }

        // Take the existing information in the "New Faction" part of the UI, build an actual
        // Faction object, and add it to the list.
        this.views.addButton.setOnClickListener {
            // The new faction's name comes from the spinner, *unless* the spinner is at
            // position 0 ("Custom..."), in which case we take the name from the editor instead.
            val name: String = if (this.views.nameSpinner.selectedItemPosition > 0) {
                this.views.nameSpinner.selectedItem.toString()
            } else {
                this.views.nameEditor.text.toString()
            }

            // Create and add the new faction. This gives us the position at which the faction
            // was added to the list, which is important because...
            val insertPosition = this.factionList.add(
                Faction(
                    name = name,
                    strength = this.views.strength.text.toString().toInt(),
                    relationship = this.views.relationship.selectedItemPosition
                )
            )

            // ... we need to notify the adapter where the new item was inserted. And we'd also
            // like to scroll to that position to ensure it's visible.
            this.views.adapter.notifyItemInserted(insertPosition)
            this.views.rvLayout.scrollToPosition(insertPosition)
        }

        return view
    }

    inner class FactionViewHolder(li: LayoutInflater, parent: ViewGroup?) :
        RecyclerView.ViewHolder(li.inflate(R.layout.list_entry, parent, false)) {

        private lateinit var fac: Faction
        private val name: EditText = itemView.findViewById(R.id.name)
        private val strength: EditText = itemView.findViewById(R.id.strength)
        private val delButton: Button = itemView.findViewById(R.id.delButton)
        private val relationship: Spinner = itemView.findViewById(R.id.relationship)
        private val tw: TextWatcher
        private val relationshipListener: OnItemSelectedListener

        fun bind(fac: Faction) {
            this.fac = fac

            // We must update the displayed name, strength and relationship. However, for each one
            // we have to temporarily disable the corresponding event handler, or else the event
            // handler would assume the *user* has edited the information.
            this.name.removeTextChangedListener(tw)
            this.name.setText(fac.name)
            this.name.addTextChangedListener(tw)
            this.strength.removeTextChangedListener(tw)
            this.strength.setText(fac.strength.toString())
            this.strength.addTextChangedListener(tw)
            this.relationship.onItemSelectedListener = null
            this.relationship.setSelection(fac.relationship.idx)
            this.relationship.onItemSelectedListener = relationshipListener
        }

        init {
            // 'tw' is an event handler that will be invoked whenever the name or the 'strength' of
            // a faction is edited. We apply the same event handler to both cases for simplicity.
            this.tw = object : TextWatcher {
                // We're required to override these methods, but we don't actually need to use
                // them.
                override fun beforeTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
                ) {}

                override fun onTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
                ) {}

                // This is where we get notified that the text has actually been changed.
                override fun afterTextChanged(editable: Editable) {
                    this@FactionViewHolder.fac.also {
                        it.name = this@FactionViewHolder.name.text.toString()
                        it.strength = this@FactionViewHolder.strength.text.toString().toInt()
                        this@MainFragment.factionList.edit(it)
                    }
                }
            }

            // An event handler invoked when the faction changes between 'enemy', 'neutral' and
            // 'ally'.
            this.relationshipListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View, position: Int, id: Long
                ) {
                    this@FactionViewHolder.fac.relationship = Relationship.from(position)
                    this@MainFragment.factionList.edit(fac)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    this@FactionViewHolder.relationship.setSelection(Relationship.from().idx)
                }
            }

            // Event handler for the 'del' button -- for deleting a faction.
            this.delButton.setOnClickListener {
                this@MainFragment.factionList.remove(fac)
                this@MainFragment.views.adapter.notifyItemRemoved(bindingAdapterPosition)
            }
        }
    }

    inner class FactionAdapter : RecyclerView.Adapter<FactionViewHolder>() {

        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): FactionViewHolder {
            return FactionViewHolder(LayoutInflater.from(this@MainFragment.activity), container)
        }

        override fun onBindViewHolder(vh: FactionViewHolder, position: Int) {
            vh.bind(this@MainFragment.factionList[position])
        }

        override fun getItemCount(): Int = this@MainFragment.factionList.size
    }

    /**
     * All views held by the fragment
     */
    data class ViewContainer(
        val nameSpinner: Spinner,
        val nameEditor: EditText,
        val strength: EditText,
        val relationship: Spinner,
        val addButton: Button,
        val rv: RecyclerView,
        val adapter: FactionAdapter,
        val rvLayout: LinearLayoutManager
    )
}