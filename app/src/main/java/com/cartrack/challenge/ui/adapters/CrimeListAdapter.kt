package com.cartrack.challenge.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.cartrack.challenge.R
import com.cartrack.challenge.api.crimes.dto.CrimeModel

class CrimeListAdapter (private val context: Context, private val items: List<CrimeModel.CrimeData>) : RecyclerView.Adapter<CrimeListAdapter.VHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.javaClass.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(LayoutInflater.from(context).inflate(R.layout.layout_user, parent, false))
    }

    override fun onBindViewHolder(viewHolder: VHolder, position: Int) {
        val item = items[position]
        viewHolder.tvName.text = "${items[position].category}"
        viewHolder.tvAddress.text = "${items[position].location.street}"
        viewHolder.itemView.setOnClickListener {

        }
    }

    class VHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvAddress = view.findViewById<TextView>(R.id.tvAddress)
    }
}
