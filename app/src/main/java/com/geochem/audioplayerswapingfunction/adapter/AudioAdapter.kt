package com.geochem.audioplayerswapingfunction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geochem.audioplayerswapingfunction.R
import kotlinx.android.synthetic.main.item_audiolist.view.*

class AudioAdapter(
    var context: Context?,

    audioList: ArrayList<HashMap<String, String>>?
): RecyclerView.Adapter<AudioAdapter.MyViewContactHolder>() {

    var audioList: ArrayList<HashMap<String, String>>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewContactHolder {
        var view=  LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audiolist, parent, false)

        return MyViewContactHolder(view)
    }

    override fun getItemCount(): Int {
        return if (audioList != null && audioList!!.size > 0) audioList!!.size else 0
    }

    override fun onBindViewHolder(holder: MyViewContactHolder, position: Int) {

        holder.onBind(position)
    }


    inner class MyViewContactHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {

            var data= audioList?.get(position)

            itemView.name.text= data?.get("songTitle") ?: "...."
           // itemView.artist.text= data?.aArtist ?: "....."
            itemView.path.text= data?.get("songPath") ?: "....."

            }


    }


    init {
        this.audioList= audioList
    }


}

