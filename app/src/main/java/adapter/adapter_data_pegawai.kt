package adapter

import android.content.Context
import android.content.Intent
import com.qiranarisqi.laundry.modeldata.modelpegawai
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.pegawai.tambahpegawai

class adapter_data_pegawai(
    private val listpegawai: ArrayList<modelpegawai>) :
    RecyclerView.Adapter<adapter_data_pegawai.ViewHolder>() {
    lateinit var appContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listpegawai[position]
        holder.tvID.text = item.idPegawai
        holder.tvnama.text = item.namaPegawai
        holder.tvalamat.text = item.alamatPegawai
        holder.tvnohp.text = item.noHPPegawai
        holder.cabang.text = item.tvcabang
        holder.cvCard.setOnClickListener{
        }

        holder.bthubungi.setOnClickListener{
        }

        holder.btlihat.setOnClickListener{
        }

        holder.cvCard.setOnClickListener{
            val intent = Intent(appContext, tambahpegawai::class.java)
            intent.putExtra("tambah", "EditPegawai")
            intent.putExtra("idPegawai", item.idPegawai)
            intent.putExtra("namaPegawai", item.namaPegawai)
            intent.putExtra("noHPPegawai", item.noHPPegawai)
            intent.putExtra("alamatPegawai", item.alamatPegawai)
            intent.putExtra("idCabang", item.tvcabang)
            appContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listpegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard =  itemView.findViewById<View>(R.id.tv_card_pegawai)
        val tvID =  itemView.findViewById<TextView>(R.id.tv_card_pegawai_id)
        val tvnama =  itemView.findViewById<TextView>(R.id.tv_nama)
        val tvalamat =  itemView.findViewById<TextView>(R.id.tv_alamat)
        val tvnohp =  itemView.findViewById<TextView>(R.id.tv_nohp)
        val cabang = itemView.findViewById<TextView>(R.id.tv_cabang)
        val bthubungi =  itemView.findViewById<Button>(R.id.bt_hubungi)
        val btlihat =  itemView.findViewById<Button>(R.id.bt_lihat)

    }


}
