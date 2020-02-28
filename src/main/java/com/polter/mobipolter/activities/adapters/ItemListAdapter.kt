package com.polter.mobipolter.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.TypedValue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.model.PredefinedListEntity

import kotlinx.android.synthetic.main.row_item_list.view.*

class ItemListAdapter(list: List<PredefinedListEntity>,isFromAddLog : Boolean, currentItem : String, context: Context, onItemClick: onItemClick) : RecyclerView.Adapter<ItemListAdapter.ItemListHolder>() {

    var itemList = list
    val ctx = context
    var listener = onItemClick
    val currentItemName = currentItem
    val isFromAddLog = isFromAddLog

    override fun onCreateViewHolder(viewholder: ViewGroup, p1: Int): ItemListHolder {

        return ItemListHolder(LayoutInflater.from(ctx).inflate(R.layout.row_item_list,viewholder,false))

    }

    override fun getItemCount(): Int {

        return itemList.size

    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: ItemListHolder, position: Int) {

        val itemEntity = itemList.get(position)
        holder.llParent.setOnClickListener {

            if(listener != null)
                listener.itemClick(itemEntity)
        }

        if(currentItemName.equals(ctx.getString(R.string.species))){

            //holder.tvItemText.setText(itemEntity.itemAbbr)

            holder.tvItemSubText.visibility = View.GONE
            if(isFromAddLog){
                holder.tvItemText.setText(itemEntity.itemAbbr)
                holder.tvItemSubText.visibility = View.GONE
                holder.tvItemLatText.visibility = View.GONE
                holder.tvItemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
               // holder.tvItemSubText.setText(itemEntity.itemName)

            }else{
                holder.tvItemText.setText(itemEntity.itemAbbr)
                holder.tvItemSubText.visibility = View.VISIBLE
                holder.tvItemLatText.visibility = View.GONE
                holder.tvItemSubText.setText(itemEntity.itemName)
            }


        }else if(currentItemName.equals(ctx.getString(R.string.kind))){

          //  holder.tvItemText.setText(itemEntity.itemLatName+" " + itemEntity.itemName +" " + itemEntity.itemAbbr)
            holder.tvItemText.setText(itemEntity.itemLatName)
            holder.tvItemSubText.setText(itemEntity.itemName)
            holder.tvItemLatText.setText("  "+itemEntity.itemAbbr)
            holder.tvItemSubText.visibility = View.VISIBLE
            holder.tvItemLatText.visibility = View.VISIBLE
            if(itemEntity.isKindSpecial){
                holder.tvItemText.setTypeface(null, Typeface.BOLD)
                holder.tvItemSubText.setTypeface(null, Typeface.BOLD)
                holder.tvItemLatText.setTypeface(null, Typeface.BOLD)
            }else{
                holder.tvItemText.setTypeface(null, Typeface.NORMAL)
                holder.tvItemSubText.setTypeface(null, Typeface.NORMAL)
                holder.tvItemLatText.setTypeface(null, Typeface.NORMAL)
            }

        }else{
            holder.tvItemText.setText(itemEntity.itemAbbr)
            holder.tvItemSubText.visibility = View.GONE
            holder.tvItemLatText.visibility = View.GONE
        }


       /* if(currentItemName.equals(ctx.getString(R.string.kind))){

            holder.tvItemText.setText(itemEntity.itemLatName+" " + itemEntity.itemName +" " + itemEntity.itemAbbr)
          //  holder.tvItemSubText.setText(itemEntity.itemName)
         //   holder.tvItemSubText.visibility = View.GONE

        }else{
            holder.tvItemText.setText(itemEntity.itemAbbr)
            holder.tvItemSubText.visibility = View.GONE
        }*/

        if(isFromAddLog){
            holder.llParent.setBackgroundResource(R.drawable.bg_dialog)
          //  holder.tvItemText.setPadding(3,3,3,3)
         //   var height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40f, ctx.getResources().getDisplayMetrics()).toInt()
// Gets the layout params that will allow you to resize the layout
         //   val params: ViewGroup.LayoutParams = holder.llParent.layoutParams
        //    params.width = ViewGroup.LayoutParams.MATCH_PARENT
        //    params.height = height
           // params.height = height
        //    holder.llParent.layoutParams = params

        }else{
            holder.llParent.setBackgroundResource(0)
            holder.tvItemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
           // holder.tvItemText.setPadding(5,5,5,5)
        }

    }



    class ItemListHolder (view: View) : RecyclerView.ViewHolder(view) {

        var tvItemText = view.tvItemText
        var tvItemSubText = view.tvItemSubText
        var llParent = view.llParent
        var tvItemLatText = view.tvItemLatText
    }
}

private fun Double.Companion.toString(logLength: Double): String {
    return logLength.toString()
}

public interface onItemClick{

    fun itemClick(entitiy : PredefinedListEntity)
}


