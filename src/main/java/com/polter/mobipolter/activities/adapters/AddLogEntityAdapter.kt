package com.polter.mobipolter.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.model.AddLogEntity
import kotlinx.android.synthetic.main.row_add_log_entity.view.*

class AddLogEntityAdapter(listenr : onLogItemClick,list: ArrayList<AddLogEntity>, context: Context) : RecyclerView.Adapter<AddLogEntityAdapter.AddLogEntityHolder>() {

    var addLogEntityList = list
    val ctx = context
    lateinit var addLogEntity: AddLogEntity
    var listener = listenr

    override fun onCreateViewHolder(viewholder: ViewGroup, p1: Int): AddLogEntityHolder {

        return AddLogEntityHolder(LayoutInflater.from(ctx).inflate(R.layout.row_add_log_entity,viewholder,false))

    }

    override fun getItemCount(): Int {

        return addLogEntityList.size

    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder:AddLogEntityHolder,position: Int) {

        addLogEntity = addLogEntityList.get(position)
        holder.tvPlate.text = addLogEntity.plate.toString()
        holder.tvSpecies.text = addLogEntity.species
        holder.tvKind.text = addLogEntity.kind
        holder.tvQuality.text = addLogEntity.quality
        holder.tvLength.text = addLogEntity.length_m.toString()
        holder.tvDiameter.text = addLogEntity.diameter_cm.toString()
        holder.tvSize.text = addLogEntity.klasse.toString()
        holder.tvOversize.text = addLogEntity.oversize_m.toString()
        holder.tvVolume.text = addLogEntity.volume_m3.toString()
        holder.tvIndex.text = addLogEntity.log_nr.toString()

        holder.container.setOnClickListener {

            listener.onLogClick(addLogEntityList.get(position))
        }

    }



    class AddLogEntityHolder (view: View) : RecyclerView.ViewHolder(view) {

        var tvPlate = view.txtAddLogPlate
        var tvSpecies = view.txtAddLogSpecies
        var tvKind = view.txtAddLogKind
        var tvQuality = view.txtAddLogQuality
        var tvLength = view.txtAddLogLength
        var tvDiameter = view.txtAddLogDiameter
        var tvSize = view.txtAddLogSize
        var tvOversize = view.txtAddLogOversize
        var tvCount = view.txtAddLogCount
        var tvVolume = view.txtAddLogVolumeM3
        var tvIndex = view.txtAddLogCounter
        var container = view.container

    }
}

private fun Double.Companion.toString(logLength: Double): String {
    return logLength.toString()

}


interface onLogItemClick{

    fun onLogClick(entity : AddLogEntity)
}


