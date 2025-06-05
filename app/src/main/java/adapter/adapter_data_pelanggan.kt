package adapter

import com.qiranarisqi.laundry.modeldata.modelpelanggan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiranarisqi.laundry.R

class adapter_data_pelanggan(
    private val listPelanggan: ArrayList<modelpelanggan>) :
    RecyclerView.Adapter<adapter_data_pelanggan.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
        ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatapelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPelanggan[position]
        holder.tvID.text = item.idPelanggan
        holder.tvnama.text = item.namaPelanggan
        holder.tvalamat.text = item.alamatPelanggan
        holder.tvnohp.text = item.noHPPelanggan
        holder.tvterdaftar.text= item.terdaftar
        holder.cvCard.setOnClickListener{
        }

        holder.bthubungi.setOnClickListener{
        }

        holder.btlihat.setOnClickListener{
        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard =  itemView.findViewById<View>(R.id.cv_card_pelanggan)
        val tvID =  itemView.findViewById<TextView>(R.id.tv_card_pelanggan_id1)
        val tvnama =  itemView.findViewById<TextView>(R.id.tv_nama1)
        val tvalamat =  itemView.findViewById<TextView>(R.id.tv_alamat1)
        val tvnohp =  itemView.findViewById<TextView>(R.id.tv_nohp1)
        val tvterdaftar =  itemView.findViewById<TextView>(R.id.tv_terdaftar1)
        val bthubungi =  itemView.findViewById<Button>(R.id.bt_hubungi1)
        val btlihat =  itemView.findViewById<Button>(R.id.bt_lihat1)

    }

}
