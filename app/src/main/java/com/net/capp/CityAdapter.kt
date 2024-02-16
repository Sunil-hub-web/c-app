package com.net.capp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(private val cityList: List<CityItem>, private val context: Context) :
    RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    private val itemClickListener: CityAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_state, parent, false)
        return CityViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cityList[position]
        holder.bind(city,context)
    }

    override fun getItemCount(): Int = cityList.size

    inner class CityViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)

        init {
            SharedPref.initialize(context)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, TermsAndConditionActivity::class.java)
                SharedPref.setCityName(cityList.get(position).name)
                SharedPref.setCityId(cityList.get(position).id)
                context.startActivity(intent)
            }
        }
        fun bind(city: CityItem, context: Context) {
            textViewName.text = city.name
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}