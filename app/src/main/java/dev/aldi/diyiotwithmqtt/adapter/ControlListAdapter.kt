package dev.aldi.diyiotwithmqtt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import dev.aldi.diyiotwithmqtt.databinding.ControlListSingleItemBinding
import dev.aldi.diyiotwithmqtt.entity.Control

class ControlListAdapter(private val controls: List<Control>): BaseAdapter() {

    override fun getCount(): Int {
        return controls.size
    }

    override fun getItem(postition: Int): Control {
        return controls[postition]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(postition: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ControlListSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val currentControl = getItem(postition)
        binding.controlName.text = currentControl.name
        binding.controlType.text = currentControl.type
        return binding.root
    }
}