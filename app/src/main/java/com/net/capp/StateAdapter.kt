package com.net.capp

import SharedPref
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StateAdapter(private val stateList: List<StateItem>, private val context: Context) :
    RecyclerView.Adapter<StateAdapter.StateViewHolder>() {
    private val itemClickListener: StateAdapter.OnClickListener?= null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_state, parent, false)
        return StateViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        val state = stateList[position]
        holder.bind(state,context)
    }

    override fun getItemCount(): Int = stateList.size

    inner class StateViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)

        init {
            SharedPref.initialize(context)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, SelectCity::class.java)
                SharedPref.setStateName(stateList.get(position).name)
                context.startActivity(intent)
            }
        }
        fun bind(state: StateItem, context: Context) {
            textViewName.text = state.name
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}