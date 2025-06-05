package adapter

import com.qiranarisqi.laundry.modeldata.modelpegawai
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modelcabang
import com.qiranarisqi.laundry.modeldata.modeltambahan

class adapter_data_tambahan(
    private val listtambahan: ArrayList<modeltambahan>) :
    RecyclerView.Adapter<adapter_data_tambahan.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listtambahan[position]
        holder.tv_id_tambahan.text = item.idtambahan
        holder.tv_nama_tambahan.text = item.namatambahan
        holder.tv_harga_tambahan.text = item.hargatambahan
        holder.tv_cabang_tambahan.text = item.cabangtambahan
        holder.tv_status_tambahan.text = item.statustambahan
        holder.cvtambahan.setOnClickListener{
        }

        holder.bt_edit_tambahan.setOnClickListener{
        }

        holder.bt_lihat_tambahan.setOnClickListener{
        }
    }

    override fun getItemCount(): Int {
        return listtambahan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvtambahan =  itemView.findViewById<View>(R.id.cvtambahan)
        val tv_id_tambahan =  itemView.findViewById<TextView>(R.id.tv_id_tambahan)
        val tv_nama_tambahan =  itemView.findViewById<TextView>(R.id.tv_nama_tambahan)
        val tv_harga_tambahan =  itemView.findViewById<TextView>(R.id.tv_harga_tambahan)
        val tv_cabang_tambahan =  itemView.findViewById<TextView>(R.id.tv_cabang_tambahan)
        val tv_status_tambahan = itemView.findViewById<TextView>(R.id.tv_status_tambahan)
        val bt_edit_tambahan =  itemView.findViewById<Button>(R.id.bt_edit_tambahan)
        val bt_lihat_tambahan =  itemView.findViewById<Button>(R.id.bt_lihat_tambahan)

    }

}
