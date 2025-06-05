package adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modelpelanggan

class adapter_pilih_pelanggan(
    private val ListTambahan: ArrayList<modelpelanggan>
) : RecyclerView.Adapter<adapter_pilih_pelanggan.ViewHolder>() {

    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_pelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = ListTambahan[position]
        holder.tvID.text = "[$nomor]"
        holder.tvNama.text = item.namaPelanggan ?: "-"
        holder.tvNoHp.text = item.noHPPelanggan ?: "-"

        holder.cvCard.setOnClickListener {
            val intent = Intent().apply {
                putExtra("idPelanggan", item.idPelanggan)
                putExtra("nama", item.namaPelanggan)
                putExtra("noHPPelanggan", item.noHPPelanggan)
            }
            if (appContext is Activity) {
                (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                (appContext as Activity).finish()
            }
        }
    }

    override fun getItemCount(): Int {
        return ListTambahan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvpiihpelanggan)
        val tvID: TextView = itemView.findViewById(R.id.tvHeaderidpelanggan)
        val tvNama: TextView = itemView.findViewById(R.id.tvnamapilihpelanggan)
        val tvNoHp: TextView = itemView.findViewById(R.id.tvNoHppilihpelanggan)
    }
}
