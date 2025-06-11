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
import com.qiranarisqi.laundry.modeldata.modellayanan
import java.text.NumberFormat
import java.util.*

class adapter_pilih_layanan(
    private val ListLayanan: ArrayList<modellayanan>,
    private val isTambahan: Boolean = false // ✅ Penanda mode tambahan atau utama
) : RecyclerView.Adapter<adapter_pilih_layanan.Viewholder>() {

    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_layanan, parent, false)
        appContext = parent.context
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val nomor = position + 1
        val item = ListLayanan[position]

        holder.tvid.text = "[$nomor]"
        holder.tvNama.text = item.namalayanan

        val hargaDouble = item.harga?.toDoubleOrNull() ?: 0.0
        holder.harga.text = "Harga = ${formatRupiah(hargaDouble)}"

        holder.cvCard.setOnClickListener {
            val intent = Intent()

            // ✅ Kirim key berbeda tergantung mode
            if (isTambahan) {
                intent.putExtra("idLayananTambahan", item.idlayanan)
                intent.putExtra("nama", item.namalayanan)
                intent.putExtra("harga", item.harga)
            } else {
                intent.putExtra("idlayanan", item.idlayanan)
                intent.putExtra("namalayanan", item.namalayanan)
                intent.putExtra("harga", item.harga)
            }

            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int = ListLayanan.size

    private fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount)
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: View = itemView.findViewById(R.id.cvpilihlayanan)
        val tvid: TextView = itemView.findViewById(R.id.tvHeaderid)
        val tvNama: TextView = itemView.findViewById(R.id.tvnamapilihlayanan)
        val harga: TextView = itemView.findViewById(R.id.tvhargapilihlayanan)
    }
}
