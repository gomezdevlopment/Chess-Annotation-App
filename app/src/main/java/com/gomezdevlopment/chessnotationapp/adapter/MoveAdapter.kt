package com.gomezdevlopment.chessnotationapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view_model.HomeViewModel

class MoveAdapter(private var notations: ArrayList<String>): RecyclerView.Adapter<MoveAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val annotation: TextView = itemView.findViewById(R.id.annotation)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val moveItem = LayoutInflater.from(parent.context).inflate(R.layout.move_item, parent, false)
        return MyViewHolder(moveItem)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentNotation = "${position+1}. ${notations[position]}"
        holder.annotation.text = currentNotation
    }

    override fun getItemCount(): Int {
        return notations.size
    }

    fun addData(list: ArrayList<String>) {
        this.notations = list
        notifyDataSetChanged()
    }
}