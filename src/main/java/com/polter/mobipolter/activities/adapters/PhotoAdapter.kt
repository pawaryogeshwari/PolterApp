package com.polter.mobipolter.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.R
import android.net.Uri
import kotlinx.android.synthetic.main.row_photos_list.view.*


class PhotoAdapter(list: ArrayList<String>,uriList: ArrayList<String>,context: Context, listener : PhotoListener) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    var photoList = list
    var photoUriList = uriList

    val ctx = context
    var listener = listener

    override fun onCreateViewHolder(viewholder: ViewGroup, p1: Int): PhotoHolder {

        return PhotoHolder(LayoutInflater.from(ctx).inflate(R.layout.row_photos_list,viewholder,false))

    }

    override fun getItemCount(): Int {

        return photoUriList.size

    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: PhotoAdapter.PhotoHolder, position: Int) {

        /*var decodedString = photoList.get(position)
        val decoded = android.util.Base64.decode(decodedString,android.util.Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decoded, 0,decoded.size)
        holder.imgPhoto.setImageBitmap(decodedBitmap)*/
        var imagePath = photoUriList.get(position)
        holder.imgPhoto.setImageURI(Uri.parse(imagePath))

        listener.onItemDisplay()

    }



    class PhotoHolder (view: View) : RecyclerView.ViewHolder(view) {

        var imgPhoto = view.imgPhoto    }
}

private fun Double.Companion.toString(logLength: Double): String {
    return logLength.toString()
}


interface PhotoListener {
    fun onItemDisplay()
}