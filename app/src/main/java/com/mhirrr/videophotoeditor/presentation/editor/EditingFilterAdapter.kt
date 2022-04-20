package com.mhirrr.videophotoeditor.presentation.editor

import GPUImageFilterTools
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhirrr.videophotoeditor.databinding.RvFilterItemBinding
import com.mhirrr.videophotoeditor.utils.Constants

class EditingFilterAdapter : RecyclerView.Adapter<EditingFilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(var binding: RvFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterViewHolder {
        return FilterViewHolder(
            RvFilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val curr: GPUImageFilterTools.FilterType = Constants.Filters[position].first
        holder.binding.rvFilterItemName.text = curr.toString().lowercase()

        holder.itemView.setOnClickListener {
            onFilterClickListener?.let {
                it(
                    curr.toString().lowercase(),
                    position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return Constants.Filters.size
    }

    private var onFilterClickListener: ((String, Int) -> Unit)? = null
    fun setOnFilterClickListener(listener: ((String, Int) -> Unit)) {
        onFilterClickListener = listener
    }

}