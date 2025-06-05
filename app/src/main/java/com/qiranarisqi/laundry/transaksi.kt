package com.qiranarisqi.laundry

import adapter.AdapterTambahanDipilih
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modeltambahan
import com.qiranarisqi.laundry.modeldata.modeltransaksitambahan
import java.text.NumberFormat
import java.util.*

class transaksi : AppCompatActivity() {

    private lateinit var tvPelangganNama : TextView
    private lateinit var tvPelangganNoHP : TextView
    private lateinit var tvLayananNama : TextView
    private lateinit var tvLayananHarga  : TextView
    private lateinit var rvLayananTambahan : RecyclerView
    private lateinit var btPilihPelanggan: Button
    private lateinit var btPilihLayanan: Button
    private lateinit var btTambahan: Button
    private lateinit var btProses: Button
    private val datalist  = mutableListOf<modeltransaksitambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan: String=""
    private var idCabang: String = ""
    private var namaPelanggan:String =""
    private var noHP:String=""
    private var idLayanan: String=""
    private var namaLayanan:String = ""
    private var hargaLayanan:String = ""
    private var idPegawai:String=""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var tambahanAdapter: AdapterTambahanDipilih

    // Fungsi untuk memformat harga ke format Indonesia
    private fun formatHarga(harga: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(harga)
    }

    // Fungsi untuk mengkonversi string harga ke integer
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang",null).toString()
        idPegawai = sharedPref.getString("idPegawai",null).toString()
        init()

        FirebaseApp.initializeApp(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        rvLayananTambahan.layoutManager = layoutManager
//        rvLayananTambahan.setHasFixedSize(true)

        rvLayananTambahan.isNestedScrollingEnabled = false
        rvLayananTambahan.setHasFixedSize(false) // Eksplisit set ke false

        tambahanAdapter = AdapterTambahanDipilih(datalist)
        rvLayananTambahan.adapter = tambahanAdapter

        btPilihPelanggan.setOnClickListener{
            val intent = Intent(this, pilihpelanggan::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btPilihLayanan.setOnClickListener{
            val intent = Intent(this, pilihlayanan::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        btTambahan.setOnClickListener{
            if (idPelanggan.isEmpty() || idLayanan.isEmpty()) {
                Toast.makeText(this, "Pilih pelanggan dan layanan utama terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, pilihlayanan::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        btProses.setOnClickListener {
            if (idPelanggan.isEmpty() || idLayanan.isEmpty()) {
                Toast.makeText(this, "Pilih pelanggan dan layanan utama terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, konfirmasidata::class.java)

            // Data pelanggan dan layanan utama
            intent.putExtra("idPelanggan", idPelanggan)
            intent.putExtra("nama", namaPelanggan)
            intent.putExtra("telepon", noHP)
            intent.putExtra("idLayanan", idLayanan)
            intent.putExtra("namalayanan", namaLayanan)

            // Convert harga dari String ke Int untuk konsistensi
            val hargaInt = parseHarga(hargaLayanan)
            intent.putExtra("hargalayanan", hargaInt)

            // Format harga untuk ditampilkan
            intent.putExtra("hargalayanan_formatted", formatHarga(hargaInt))

            // Kirim data layanan tambahan sebagai ArrayList sederhana
            val tambahanNamaList = ArrayList<String>()
            val tambahanHargaList = ArrayList<Int>()
            val tambahanHargaFormattedList = ArrayList<String>()

            for (item in datalist) {
                tambahanNamaList.add(item.nama ?: "")
                val hargaTambahan = item.harga ?: 0
                tambahanHargaList.add(hargaTambahan)
                tambahanHargaFormattedList.add(formatHarga(hargaTambahan))
            }

            intent.putStringArrayListExtra("tambahan_nama", tambahanNamaList)
            intent.putIntegerArrayListExtra("tambahan_harga", tambahanHargaList)
            intent.putStringArrayListExtra("tambahan_harga_formatted", tambahanHargaFormattedList)

            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun init(){
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == pilihPelanggan){
            if(resultCode == RESULT_OK && data!= null) {
                idPelanggan = data.getStringExtra("idPelanggan").toString()
                val nama = data.getStringExtra("nama")
                val nomorHP = data.getStringExtra("noHPPelanggan")
                tvPelangganNama.text = "Nama Pelanggan : $nama"
                tvPelangganNoHP.text = "No HP : $nomorHP"
                namaPelanggan = nama.toString()
                noHP = nomorHP.toString()
            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Batal memilih pelanggan $namaPelanggan", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if(requestCode == pilihLayanan){
            if(resultCode == RESULT_OK && data!= null) {
                idLayanan = data.getStringExtra("idlayanan").toString()
                val nama = data.getStringExtra("namalayanan")
                val harga = data.getStringExtra("harga")

                // Format harga untuk tampilan
                val hargaInt = parseHarga(harga.toString())
                val hargaFormatted = formatHarga(hargaInt)

                tvLayananNama.text = "Nama Layanan : $nama"
                tvLayananHarga.text = "Harga : $hargaFormatted"
                namaLayanan = nama.toString()
                hargaLayanan = harga.toString()
            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Batal memilih layanan $namaLayanan", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Modifikasi pada bagian onActivityResult di class Transaksi
// Ganti bagian untuk pilihLayananTambahan dengan kode berikut:

        if (requestCode == pilihLayananTambahan) {
            if (resultCode == RESULT_OK && data != null) {
                val idLayananTambahan = data.getStringExtra("idLayananTambahan").toString()
                val namaLayanan = data.getStringExtra("nama").toString()
                val hargaLayanan = data.getStringExtra("harga").toString()

                // Convert harga dari String ke Int
                val hargaInt = parseHarga(hargaLayanan)

                val modelTambahan = modeltransaksitambahan(idLayananTambahan, namaLayanan, hargaInt)
                datalist.add(modelTambahan)

                // Tampilkan Toast ketika item berhasil ditambahkan
                Toast.makeText(this, "$namaLayanan berhasil ditambahkan", Toast.LENGTH_SHORT).show()

                tambahanAdapter.notifyDataSetChanged()
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Batal menambah layanan tambahan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}