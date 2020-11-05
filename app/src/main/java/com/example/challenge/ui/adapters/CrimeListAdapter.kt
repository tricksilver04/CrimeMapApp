package com.example.challenge.ui.adapters;

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.challenge.api.crimes.dto.CrimeModel
import com.example.challenge.databinding.LayoutCrimeRowBinding
import java.util.ArrayList

class CrimeListAdapter (private val context: Context, private val items: ArrayList<CrimeModel.CrimeData>) : RecyclerView.Adapter<CrimeListAdapter.VHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.javaClass.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val binding = LayoutCrimeRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return VHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: VHolder, position: Int) {
        val item = items[position]
        viewHolder.bind(item)
    }

    class VHolder (private val layoutUserBinding: LayoutCrimeRowBinding) : RecyclerView.ViewHolder(layoutUserBinding.root) {
        fun bind(item: CrimeModel.CrimeData){
            layoutUserBinding.tvName.text = "Crime:\t[${item.id}] ${item.category}"
            layoutUserBinding.tvAddress.text = "Location:\t${item.location.street.name}"
        }
    }
}
