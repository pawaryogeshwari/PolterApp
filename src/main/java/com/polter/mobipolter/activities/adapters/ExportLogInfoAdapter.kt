package com.polter.mobipolter.activities.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.model.AddLogEntity
import kotlinx.android.synthetic.main.list_row_child.view.*
import kotlinx.android.synthetic.main.row_add_log_entity.view.*
import android.widget.CompoundButton
import android.widget.CheckBox




class ExportLogInfoAdapter(listenr: onLogItemClick, list: ArrayList<AddLogEntity>, context: Context, stacknr: String) : RecyclerView.Adapter<ExportLogInfoAdapter.AddLogEntityHolder>() {


    var addLogEntityList = list
    val ctx = context
    lateinit var addLogEntity: AddLogEntity
    var listener = listenr
    var stackNrValue = stacknr
    public var selected_logentry_list = ArrayList<AddLogEntity>()


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ExportLogInfoAdapter.AddLogEntityHolder {

        return ExportLogInfoAdapter.AddLogEntityHolder(LayoutInflater.from(ctx).inflate(com.polter.mobipolter.R.layout.list_row_child, p0, false))

    }

    override fun getItemCount(): Int {

        return addLogEntityList.size

    }

    override fun onBindViewHolder(holder: AddLogEntityHolder, position: Int) {

        addLogEntity = addLogEntityList.get(position)
        holder.cbSelected.tag = addLogEntityList.get(position)
        holder.cbSelected.setChecked(addLogEntity.isSelected);
        holder.polterNo.text = stackNrValue + "." + addLogEntityList.get(position).kind

//        addLogEntity.isSelected = addLogEntityList[position].isSelected
        /*holder.cbSelected.setOnCheckedChangeListener { compoundButton, bool ->
            if (bool)
                addLogEntity.isSelected = true
            else
                addLogEntity.isSelected = false
        }*/

        if (addLogEntity.isSelected) {
            holder.cbSelected.setChecked(true);
        }
        else {
            holder.cbSelected.setChecked(false);
        }



        holder.cbSelected.setOnClickListener({ view ->

            val cb = view as CheckBox
            val entity : AddLogEntity  = cb.tag as AddLogEntity
            entity.isSelected = cb.isSelected
            addLogEntityList.get(position).isSelected = cb.isSelected
            listener.onLogClick( addLogEntityList.get(position))
        })




        /*  {
              if(selected_logentry_list.contains(addLogEntityList.get(position))){
                  holder.isSelected.visibility = View.VISIBLE
                  holder.container.setBackgroundColor(ctx.resources.getColor(R.color.item_selected_color))
                  holder.isSelected.isChecked = true

              }


              else{
                  holder.isSelected.isChecked = false

              }



          }
  */

    }


    class AddLogEntityHolder(view: View) : RecyclerView.ViewHolder(view) {

        var polterNo = view.textViewChild

        var container = view.expContainer

        var cbSelected = view.cbSelected


    }

    interface onLogItemClick {
        fun onLogClick(entity: AddLogEntity)
    }


}
