package com.polter.mobipolter.activities.adapters
import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.model.SizeClassEntity
import kotlinx.android.synthetic.main.row_size_class_entity.view.*

class SizeClassEntityAdapter(list: ArrayList<SizeClassEntity>, context: Context) : RecyclerView.Adapter<SizeClassEntityAdapter.SizeClassEntityHolder>() {

    var sizeClassEntityList = list
    val ctx = context
    lateinit var sizeClassEntity: SizeClassEntity

    override fun onCreateViewHolder(viewholder: ViewGroup, p1: Int): SizeClassEntityHolder {

        return SizeClassEntityHolder(LayoutInflater.from(ctx).inflate(R.layout.row_size_class_entity,viewholder,false))

    }

    override fun getItemCount(): Int {

        return sizeClassEntityList.size

    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: SizeClassEntityHolder, position: Int) {

        sizeClassEntity = sizeClassEntityList.get(position)
        holder.tvSize.text = sizeClassEntity.size
        holder.tvSizeCount.text = sizeClassEntity.count.toString()
        holder.tvSizeQuota.text = sizeClassEntity.quota.toString()
        holder.tvSizeVolume.text = sizeClassEntity.volume_m3.toString()


    }



    class SizeClassEntityHolder (view: View) : RecyclerView.ViewHolder(view) {

        var tvSize = view.txtSize
        var tvSizeCount = view.txtSizeCount
        var tvSizeQuota = view.txtSizeQuota
        var tvSizeVolume = view.txtSizeVolume

    }
}

private fun Double.Companion.toString(logLength: Double): String {
    return logLength.toString()
}


