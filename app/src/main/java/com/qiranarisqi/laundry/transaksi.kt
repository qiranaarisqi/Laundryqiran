package com.qiranarisqi.laundry

import adapter.AdapterTambahanDipilih
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.qiranarisqi.laundry.modeldata.modeltambahan
import com.qiranarisqi.laundry.modeldata.modeltransaksitambahan
import java.text.NumberFormat
import java.util.*

class transaksi : AppCompatActivity() {

    private lateinit var tvPelangganNama: TextView
    private lateinit var tvPelangganNoHP: TextView
    private lateinit var tvLayananNama: TextView
    private lateinit var tvLayananHarga: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var btPilihPelanggan: Button
    private lateinit var btPilihLayanan: Button
    private lateinit var btTambahan: Button
    private lateinit var btProses: Button
    private val datalist = mutableListOf<modeltransaksitambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan: String = ""
    private var idCabang: String = ""
    private var namaPelanggan: String = ""
    private var noHP: String = ""
    private var idLayanan: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = ""
    private var idPegawai: String = ""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var tambahanAdapter: AdapterTambahanDipilih

    private fun formatHarga(harga: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(harga)
    }

    private fun parseHarga(hargaString: String): Int {
        return try {
            hargaString.replace("Rp", "")
                .replace(".", "")
                .replace(",", "")
                .replace(" ", "")
                .trim()
                .toInt()
        } catch (e: Exception) {
            0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", null).toString()
        idPegawai = sharedPref.getString("idPegawai", null).toString()

        init()

        FirebaseApp.initializeApp(this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        rvLayananTambahan.layoutManager = layoutManager
        rvLayananTambahan.setHasFixedSize(false)
        rvLayananTambahan.isNestedScrollingEnabled = false

        tambahanAdapter = AdapterTambahanDipilih(datalist)
        rvLayananTambahan.adapter = tambahanAdapter

        btPilihPelanggan.setOnClickListener {
            val intent = Intent(this, pilihpelanggan::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btPilihLayanan.setOnClickListener {
            val intent = Intent(this, pilihlayanan::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        btTambahan.setOnClickListener {
            if (idPelanggan.isEmpty() || idLayanan.isEmpty()) {
                Toast.makeText(this, "Pilih pelanggan dan layanan utama terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, data_tambahan_item::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        btProses.setOnClickListener {
            if (idPelanggan.isEmpty() || idLayanan.isEmpty()) {
                Toast.makeText(this, "Pilih pelanggan dan layanan utama terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, konfirmasidata::class.java)

            intent.putExtra("namaPelanggan", "Nama: $namaPelanggan")
            intent.putExtra("noHP", "No HP: $noHP")
            intent.putExtra("namaLayanan", "Layanan: $namaLayanan")

            val hargaInt = parseHarga(hargaLayanan)
            intent.putExtra("hargaLayanan", hargaInt)

            // Convert datalist ke ArrayList<modeltambahan>
            val tambahanList = ArrayList<modeltambahan>()
            for (item in datalist) {
                val tambahan = modeltambahan(
                    idtambahan = item.idLayananTambahan,
                    namatambahan = item.nama,
                    hargatambahan = item.harga
                )
                tambahanList.add(tambahan)
            }
            intent.putParcelableArrayListExtra("tambahanList", tambahanList)

            startActivity(intent)
        }
    }

    private fun init() {
        tvPelangganNama = findViewById(R.id.namapelanggantransaksi)
        tvPelangganNoHP = findViewById(R.id.nohptransaksi)
        tvLayananNama = findViewById(R.id.namalayanantransaksi)
        tvLayananHarga = findViewById(R.id.hargatransaksi)
        rvLayananTambahan = findViewById(R.id.rvTransaksilayanantambahan)
        btPilihPelanggan = findViewById(R.id.btPilihPelanggan)
        btPilihLayanan = findViewById(R.id.btPilihLayanan)
        btTambahan = findViewById(R.id.btPilihTambahan)
        btProses = findViewById(R.id.prosestransaksi)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pilihPelanggan && resultCode == RESULT_OK && data != null) {
            idPelanggan = data.getStringExtra("idPelanggan").orEmpty()
            val nama = data.getStringExtra("nama")
            val nomorHP = data.getStringExtra("noHPPelanggan")
            tvPelangganNama.text = "Nama Pelanggan : $nama"
            tvPelangganNoHP.text = "No HP : $nomorHP"
            namaPelanggan = nama ?: ""
            noHP = nomorHP ?: ""
        }

        if (requestCode == pilihLayanan && resultCode == RESULT_OK && data != null) {
            idLayanan = data.getStringExtra("idlayanan").orEmpty()
            val nama = data.getStringExtra("namalayanan")
            val harga = data.getStringExtra("harga")

            val hargaInt = parseHarga(harga.orEmpty())
            val hargaFormatted = formatHarga(hargaInt)

            tvLayananNama.text = "Nama Layanan : $nama"
            tvLayananHarga.text = "Harga : $hargaFormatted"
            namaLayanan = nama ?: ""
            hargaLayanan = harga ?: ""
        }

        if (requestCode == pilihLayananTambahan && resultCode == RESULT_OK && data != null) {
            val idTambahan = data.getStringExtra("idtambahan").orEmpty()
            val nama = data.getStringExtra("namatambahan").orEmpty()
            val harga = data.getStringExtra("hargatambahan").orEmpty()

            val tambahan = modeltransaksitambahan(idTambahan, nama, harga)
            datalist.add(tambahan)
            tambahanAdapter.notifyItemInserted(datalist.size - 1)

            Toast.makeText(this, "$nama berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }
}
