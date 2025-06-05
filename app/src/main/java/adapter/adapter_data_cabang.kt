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

class adapter_data_cabang(
    private val listcabang: ArrayList<modelcabang>) :
    RecyclerView.Adapter<adapter_data_cabang.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_cabang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listcabang[position]
        holder.tv_id_cabang.text = item.idcabang
        holder.tv_nama_cabang.text = item.namacabang
        holder.tv_alamat.text = item.alamatcabang
        holder.tv_telepon.text = item.teleponcabang
        holder.tv_layanan.text = item.layanancabang
        holder.cvCabang.setOnClickListener{
        }

        holder.bt_hubungicabang.setOnClickListener{
        }

        holder.bt_lihatcabang.setOnClickListener{
        }
    }

    override fun getItemCount(): Int {
        return listcabang.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCabang =  itemView.findViewById<View>(R.id.cvCabang)
        val tv_id_cabang =  itemView.findViewById<TextView>(R.id.tv_id_cabang)
        val tv_nama_cabang =  itemView.findViewById<TextView>(R.id.tv_nama_cabang)
        val tv_alamat =  itemView.findViewById<TextView>(R.id.tv_alamat)
        val tv_telepon =  itemView.findViewById<TextView>(R.id.tv_telepon)
        val tv_layanan = itemView.findViewById<TextView>(R.id.tv_layanan)
        val bt_hubungicabang =  itemView.findViewById<Button>(R.id.bt_hubungicabang_tambahan)
        val bt_lihatcabang =  itemView.findViewById<Button>(R.id.bt_lihatcabang_tambahan)

    }

}
