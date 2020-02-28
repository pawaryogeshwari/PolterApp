package com.polter.mobipolter.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.R

import com.polter.mobipolter.activities.model.MeasureHeightListEntity
import kotlinx.android.synthetic.main.row_measure_height_entry.view.*

class HeightEntryAdapter(list: ArrayList<MeasureHeightListEntity>, context: Context) : RecyclerView.Adapter<HeightEntryAdapter.HeightEntryHolder>() {

    var heightEntryList = list
    val ctx = context
    lateinit var measureHeightListEntity: MeasureHeightListEntity

    override fun onCreateViewHolder(viewholder: ViewGroup, p1: Int): HeightEntryHolder {

        return HeightEntryHolder(LayoutInflater.from(ctx).inflate(R.layout.row_measure_height_entry,viewholder,false))

    }

    override fun getItemCount(): Int {

        return heightEntryList.size

    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: HeightEntryHolder, position: Int) {

        measureHeightListEntity = heightEntryList.get(position)
        holder.tvIndex.text = measureHeightListEntity.index.toString()
        holder.tvHeight.text = measureHeightListEntity.height.toString()
        holder.tvSection.text = measureHeightListEntity.section.toString()

    }



    class HeightEntryHolder (view: View) : RecyclerView.ViewHolder(view) {

        var tvHeight = view.txtHeightEntry
        var tvSection = view.txtSectionEntry
        var tvIndex = view.txtIndex
    }
}

private fun Double.Companion.toString(logLength: Double): String {
    return logLength.toString()
}


