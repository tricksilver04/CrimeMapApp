package com.example.challenge.ui.adapters;

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.challenge.api.crimes.dto.CrimeModel
import com.example.challenge.databinding.DialogCrimeDetailsBinding
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
            layoutUserBinding.root.setOnClickListener {
                val binding = DialogCrimeDetailsBinding
                    .inflate(LayoutInflater.from(layoutUserBinding.root.context))
                binding.tvCategory.text = "Category: ${item.category}"
                binding.tvId.text = "Category: ${item.id}"
                binding.tvLocation.text = "Location: ${item.location.latitude},${item.location.longitude}"
                binding.tvStreetName.text = "Street: ${item.location.street.name}"
                binding.tvStreetId.text = "Street Id: ${item.location.street.id}"
                binding.tvLocationType.text = "Location Type: ${item.location_type}"
                binding.tvMonth.text = "Month: ${item.month}"

                val builder = AlertDialog.Builder(layoutUserBinding.root.context)
                builder.setTitle("Details")
                builder.setView(binding.root)
                builder.setPositiveButton("OK") { dialog, _ ->
                   dialog.dismiss()
                }
                builder.show()
            }
        }
    }
}
