package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qiranarisqi.laundry.modeldata.modellayanan
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modeltambahan

class adapter_konfirmasi (
    private val listlayanan: ArrayList<modeltambahan>):
        RecyclerView.Adapter<adapter_konfirmasi.ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_konfirmasidata, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: adapter_konfirmasi.ViewHolder, position: Int) {
        val item = listlayanan[position]
        holder.idlayanan.text = item.idtambahan ?: "-"
        holder.namalayanan.text = item.namatambahan
        holder.harga.text = item.hargatambahan

        holder.card_data_layanan.setOnClickListener(){

        }
    }

    override fun getItemCount(): Int {
        return listlayanan.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card_data_layanan =  itemView.findViewById<View>(R.id.cvKonvirmasidata_tambahan)
        val idlayanan =  itemView.findViewById<TextView>(R.id.idKDtambahan)
        val namalayanan =  itemView.findViewById<TextView>(R.id.tvnamatambahanKD)
        val harga =  itemView.findViewById<TextView>(R.id.tvhargatambahanKD)
    }
        }



