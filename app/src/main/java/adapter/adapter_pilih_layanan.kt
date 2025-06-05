package adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modellayanan
import com.qiranarisqi.laundry.modeldata.modeltambahan
import java.text.NumberFormat
import java.util.Locale

class adapter_pilih_layanan(private val ListTambahan: ArrayList<modellayanan>) : RecyclerView.Adapter<adapter_pilih_layanan.Viewholder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_layanan, parent, false)
        appContext = parent.context
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: adapter_pilih_layanan.Viewholder, position: Int) {
        val nomor = position + 1
        val item = ListTambahan[position]
        holder.tvid.text = "[$nomor]"
        holder.tvNama.text = item.namalayanan
        val hargaDouble = item.harga?.toDoubleOrNull() ?: 0.0
        holder.harga.text = "Harga= ${formatRupiah(hargaDouble)}"

        holder.cvCard.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idlayanan", item.idlayanan)
            intent.putExtra("namalayanan", item.namalayanan)
            intent.putExtra("harga", item.harga)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }
    private fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount)
    }

    override fun getItemCount(): Int {
        return ListTambahan.size
    }

    class Viewholder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val cvCard = itemView.findViewById<View>(R.id.cvpilihlayanan)
        val tvid = itemView.findViewById<TextView>(R.id.tvHeaderid)
        val tvNama = itemView.findViewById<TextView>(R.id.tvnamapilihlayanan)
        val harga = itemView.findViewById<TextView>(R.id.tvhargapilihlayanan)
    }
}