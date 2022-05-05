package com.gomezdevlopment.chessnotationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoveAdapter(private val notations: ArrayList<String>): RecyclerView.Adapter<MoveAdapter.MyViewHolder>() {


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

}