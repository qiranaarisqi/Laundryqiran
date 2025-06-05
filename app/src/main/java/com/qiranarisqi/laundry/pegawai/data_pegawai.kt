package com.qiranarisqi.laundry.pegawai

import adapter.adapter_data_pegawai
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modelpegawai
import com.qiranarisqi.laundry.pelanggan.TambahPelanggan
import com.qiranarisqi.laundry.pegawai.tambahpegawai

//import com.qiranarisqi.laundry.pegawai.TambahPegawaiActivity


class data_pegawai : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var rv_data_pegawai: RecyclerView
    lateinit var tambah2: FloatingActionButton
    lateinit var listpegawai: ArrayList<modelpegawai>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rv_data_pegawai.layoutManager = layoutManager
        rv_data_pegawai.setHasFixedSize(true)

        listpegawai = arrayListOf<modelpegawai>() // Inisialisasi listPelanggan

        getData()
        tambah2.setOnClickListener{
            val intent = Intent(this, tambahpegawai::class.java) // Fix target activity
            intent.putExtra("tvjudul", this.getString(R.string.card_tambah_pegawai)) // Fix key
            intent.putExtra("idPegawai", "")
            intent.putExtra("namaPegawai", "")
            intent.putExtra("noHPPegawai", "")
            intent.putExtra("alamatPegawai", "")
            intent.putExtra("tvcabang", "") // Fix key name
            startActivity(intent)
        }

        tekan()

        // Untuk mengatur padding pada sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rv_data_pegawai = findViewById(R.id.rv_data_pegawai)
        tambah2 = findViewById(R.id.bt_data_pengguna_tambah) // Pastikan ini adalah FloatingActionButton
    }

    fun getData() {
        val query = myRef.orderByChild("idpegawai").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listpegawai.clear() // Bersihkan list sebelum mengisi data baru
                    for (dataSnapshot in snapshot.children) { // Perbaiki menjadi snapshot.children
                        val pegawai = dataSnapshot.getValue(modelpegawai::class.java)
                        listpegawai.add(pegawai!!)

                    }
                    val adapter = adapter_data_pegawai(listpegawai)
                    rv_data_pegawai.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error saat gagal mengambil data
                Toast.makeText(this@data_pegawai, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun tekan() {
        tambah2.setOnClickListener {
            val intent = Intent(this, tambahpegawai::class.java)
            startActivity(intent)
        }
    }
}