package com.polter.mobipolter.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.DashboardActivity
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.model.LogStackEntity
import kotlinx.android.synthetic.main.row_dashboard_log_item.view.*

class DashboardLogAdapter(list: ArrayList<LogStackEntity>, selected_logs_list: ArrayList<LogStackEntity>, context: Context, listener : DashboardLogListener) : RecyclerView.Adapter<DashboardLogAdapter.DashboardLogEntryHolder>() {

    var logEntryList = list
    var ctx = context
    public var selected_logentry_list = selected_logs_list
    var multiSelect  = false
    var mListener = listener
    var enableSelection = false
    var showSelectedItems = false
    var activateContainer = true

    override fun onCreateViewHolder(viewholder: ViewGroup, p1: Int): DashboardLogEntryHolder {

        return DashboardLogEntryHolder(LayoutInflater.from(ctx).inflate(R.layout.row_dashboard_log_item,viewholder,false))

    }

    override fun getItemCount(): Int {

        return logEntryList.size

    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: DashboardLogEntryHolder, position: Int) {

        var logEntry = logEntryList.get(position)

        if(enableSelection){
            holder.logTest.visibility = View.VISIBLE;

        }else{
            holder.logTest.visibility = View.GONE;
        }

        holder.logTitle.text = logEntry.logBasicEntity?.stackNR
    //    holder.logForestOwner.text =
        holder.logDate.text = logEntry.logDate
        if(!TextUtils.isEmpty(logEntry.logSpeciesCount)){
            holder.logSpecies.text = ctx.resources.getString(R.string.species_text,logEntry.logSpeciesCount)
            holder.logSpecies.setCompoundDrawables(null,null,null,null)
        }else{
            holder.logSpecies.text = ctx.resources.getString(R.string.species_text,"")
            holder.logSpecies.setCompoundDrawables(null,null,ctx.resources.getDrawable(R.drawable.ic_cloudd),null)
        }
        if(!TextUtils.isEmpty(logEntry.logKind)){
            holder.logKind.text = logEntry.logKind
           // holder.logKind.setCompoundDrawables(null,null,null,null)
        }else{
            holder.logKind.text = ""
           // holder.logKind.setCompoundDrawables(null,null,ctx.resources.getDrawable(R.drawable.ic_cloudd),null)
        }
        if(!TextUtils.isEmpty(logEntry.logQuality)){
            holder.logQuality.text = ctx.resources.getString(R.string.quality_text,logEntry.logQuality)
            holder.logQuality.setCompoundDrawables(null,null,null,null)
        }else{
            holder.logQuality.text = ctx.resources.getString(R.string.quality_text,"")

            holder.logQuality.setCompoundDrawables(null,null,ctx.resources.getDrawable(R.drawable.ic_cloudd),null)
        }

        holder.logLength.text = ctx.resources.getString(R.string.length_text,Double.toString(logEntry.logLength))
        if(logEntry?.logMeasurementEntity?.sectionEntity != null){
            holder.logVolume.text = Double.toString(logEntry?.logMeasurementEntity?.sectionEntity?.m3!!)
        }else{
            holder.logVolume.text = "0.00"
        }

        if(logEntry.logBasicEntity?.iosID != null){
            holder.logLosID.text = ctx.resources.getString(R.string.los_id_text,logEntry.logBasicEntity?.iosID)
        }else{
            holder.logLosID.text = ctx.resources.getString(R.string.los_id_text,"")
        }


        holder.logCount.text = ctx.resources.getString(R.string.log_count_homescreen,logEntry?.logCount.toString())
        holder.logsVolume.text = ctx.resources.getString(R.string.log_volume,logEntry?.logMeasurementEntity?.logsEntity?.total_volume_m3.toString())


        if(showSelectedItems){
            if(selected_logentry_list.contains(logEntryList.get(position))){
                holder.logTest.visibility = View.VISIBLE
                holder.logTest.setImageResource(R.drawable.bg_checked)
                holder.container.setBackgroundColor(ctx.resources.getColor(R.color.item_selected_color))

            }else{
                holder.logTest.visibility = View.VISIBLE
                holder.logTest.setImageResource(R.drawable.bg_unchecked)
                holder.container.setBackgroundColor(ctx.resources.getColor(R.color.gray_color))

            }

        }else{
            holder.logTest.setImageResource(R.drawable.bg_unchecked)
            val recentID = ((ctx) as DashboardActivity).mAppSharedPreferences.getInt("recentLogId",1)
            if(recentID == logEntry.logId){
                holder.container.setBackgroundColor(ctx.resources.getColor(R.color.gray_color))
            }else{
                holder.container.setBackgroundColor(ctx.resources.getColor(R.color.white_color))
            }
        //    holder.container.setBackgroundColor(ctx.resources.getColor(R.color.gray_color))

        }

        holder.container.setOnClickListener{
            System.out.println("Log clicked")


            if(multiSelect){

                mListener.onCheckboxImageClick(position)

            }else{

                mListener.onContainerClick(logEntry)
               /* System.out.println("Log opening LogEntryDetailActivity")
                ctx.startActivity(Intent(ctx, LogEntryDetailActivity::class.java)
                            .putExtra("logId",logEntry.logId)
                            .putExtra("stackNR",logEntry.logTitle)
                            .putExtra("recentTab",logEntry.recentTab))*/
            }
        }

        if(logEntry.surveyingType.equals(ctx.getString(R.string.logs_surveying))){
           holder.llSpeciesLayout.visibility = View.GONE
           holder.llLengthLayout.visibility = View.GONE
            holder.llLogsLayout.visibility = View.VISIBLE
            holder.logDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_log,0,0,0)
        }else{
            holder.llSpeciesLayout.visibility = View.VISIBLE
            holder.llLengthLayout.visibility = View.VISIBLE
            holder.llLogsLayout.visibility = View.GONE
            if(logEntry.surveyingType.equals(ctx.getString(R.string.section_surveying))){
                holder.logDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_section,0,0,0)
            }else{
                holder.logDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_estimation,0,0,0)

            }
        }


    }



    class DashboardLogEntryHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
      //  val tvAnimalType = view.tv_animal_type
        var logTitle = view.txtLogTitle
        var logDate = view.txtDate
        var logSpecies = view.txtSpecies
        var logKind = view.txtKind
        var logQuality = view.txtQuality
        var logLength = view.txtLength
        var logVolume = view.txtVolume
        var logLosID = view.txtForestOwner
        var logTest = view.test
        var container = view.container
        var llSpeciesLayout = view.llSpeciesLayout
        var llLengthLayout = view.llLengthLayout
        var llLogsLayout = view.llLogLayout
        var logCount = view.txtLogCount
        var logsVolume = view.txtLogVolume

    }

    fun activateContainer(activate: Boolean) {
        this.activateContainer = activate
        notifyDataSetChanged()
    }
}

private fun Double.Companion.toString(logLength: Double): String {
    return logLength.toString()
}

interface DashboardLogListener{

    fun onCheckboxImageClick(position: Int)
    fun onContainerClick(logEntry : LogStackEntity)
}

