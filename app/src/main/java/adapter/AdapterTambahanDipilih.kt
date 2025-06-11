package adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modeltambahan
import com.qiranarisqi.laundry.modeldata.modeltransaksitambahan
import java.text.NumberFormat
import java.util.Locale

class AdapterTambahanDipilih(
    private val ListTambahan:MutableList<modeltransaksitambahan>,
    private val onItemRemoved: (() -> Unit)? = null
) : RecyclerView.Adapter<AdapterTambahanDipilih.Viewholder>() {

    private lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_itemlayanantambahan, parent, false)
        appContext = parent.context
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val nomor = position + 1
        val item = ListTambahan[position]

        holder.tvid.text = "[$nomor]"
        holder.tvNama.text = item.nama

        val harga = item.harga?.toIntOrNull() ?: 0
        holder.harga.text = "Harga = ${formatRupiah(harga)}"


        holder.cvCard.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idLayananTambahan", item.idLayananTambahan)
            intent.putExtra("nama", item.nama)
            intent.putExtra("harga", item.harga)

            // Disable klik agar tidak bisa diklik lagi
            holder.cvCard.isClickable = false
            holder.cvCard.isFocusable = false
        }

        holder.btnHapus.setOnClickListener {
            val namaItem = item.nama ?: "Item"
            ListTambahan.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, ListTambahan.size)
            Toast.makeText(appContext, "$namaItem berhasil dihapus", Toast.LENGTH_SHORT).show()
            onItemRemoved?.invoke()
        }
    }

    override fun getItemCount(): Int = ListTambahan.size
    private fun formatRupiah(number: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(number)
    }



    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: View = itemView.findViewById(R.id.cv_card_item_tambahan2)
        val tvid: TextView = itemView.findViewById(R.id.tv_id_layanantambahan)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama_layanantambahan)
        val harga: TextView = itemView.findViewById(R.id.tv_hargalayanantambahan)
        val btnHapus: ImageView = itemView.findViewById(R.id.btnhapus)
    }
}
