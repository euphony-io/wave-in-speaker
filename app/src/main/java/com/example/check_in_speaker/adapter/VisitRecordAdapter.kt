package com.example.check_in_speaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.check_in_speaker.databinding.ItemVisitRecordBinding
import com.example.check_in_speaker.db.User

class VisitRecordAdapter(private val visitList: ArrayList<User>): RecyclerView.Adapter<VisitRecordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVisitRecordBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = visitList[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemVisitRecordBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            binding.tvRecyclerviewLayoutDate.text = user.date
            binding.tvRecyclerviewLayoutAddress.text = user.address
        }
    }

    override fun getItemCount(): Int = visitList.size
}