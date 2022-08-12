package dev.aldi.diyiotwithmqtt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.aldi.diyiotwithmqtt.databinding.ControlListSingleItemBinding
import dev.aldi.diyiotwithmqtt.entity.Control

class ControlListAdapter(private val controls: List<Control>): RecyclerView.Adapter<ControlListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ControlListSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder) {
            with (controls[position]) {
                binding.controlName.text = "${this.name} (${this.type})"
                binding.controlType.text = this.type
            }
        }
    }

    override fun getItemCount(): Int {
        return controls.size
    }

    inner class ViewHolder(val binding: ControlListSingleItemBinding): RecyclerView.ViewHolder(binding.root)
}