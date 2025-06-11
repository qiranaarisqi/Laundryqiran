package adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modeltambahan
import java.text.NumberFormat
import java.util.*

class Adapterlayanantambahan(
    private val listLayanan: ArrayList<modeltambahan>,
) : RecyclerView.Adapter<Adapterlayanantambahan.Viewholder>() {

    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_item_tambahan, parent, false)
        appContext = parent.context
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val nomor = position + 1
        val item = listLayanan[position]

        holder.tvid.text = "[$nomor]"
        holder.tvNama.text = item.namatambahan

        val hargaDouble = item.hargatambahan?.toDoubleOrNull() ?: 0.0
        holder.harga.text = "Harga = ${formatRupiah(hargaDouble)}"

        holder.cvCard.setOnClickListener {
            val intent = Intent()

            // Selalu kirim data, tidak tergantung isTambahan
            intent.putExtra("idtambahan", item.idtambahan)
            intent.putExtra("namatambahan", item.namatambahan)
            intent.putExtra("hargatambahan", item.hargatambahan)

            if (appContext is Activity) {
                (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                (appContext as Activity).finish()
            }
        }

    }

    override fun getItemCount(): Int = listLayanan.size

    private fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount)
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: View = itemView.findViewById(R.id.cv_card_item_tambahan)
        val tvid: TextView = itemView.findViewById(R.id.tv_id_layanantambahan)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama_layanantambahan)
        val harga: TextView = itemView.findViewById(R.id.tv_hargalayanantambahan)
    }
}
