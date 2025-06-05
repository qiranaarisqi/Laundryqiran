package com.qiranarisqi.laundry.pelanggan

import adapter.adapter_data_pelanggan
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
import com.google.firebase.database.*
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modelpelanggan

class datapelanggan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rv_data_pelanggan: RecyclerView
    lateinit var tambah2: FloatingActionButton
    lateinit var listPelanggan: ArrayList<modelpelanggan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datapelanggan)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rv_data_pelanggan.layoutManager = layoutManager
        rv_data_pelanggan.setHasFixedSize(true)

        listPelanggan = arrayListOf<modelpelanggan>() // Inisialisasi listPelanggan

        getData()
        tambah2.setOnClickListener{
            val intent = Intent(this, TambahPelanggan::class.java)
            intent.putExtra("judul", this.getString(R.string.card_tambah_pegawai))
            intent.putExtra("idPelanggan", "")
            intent.putExtra("namaPelanggan", "")
            intent.putExtra("noHPPelanggan", "")
            intent.putExtra("alamatPelanggan", "")
            startActivity(intent)
        }
//        tekan()

        // Untuk mengatur padding pada sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rv_data_pelanggan = findViewById(R.id.rv_data_pelanggan1)
        tambah2 = findViewById(R.id.fab_data_pengguna_tambah1) // Pastikan ini adalah FloatingActionButton
    }

    fun getData() {
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listPelanggan.clear() // Bersihkan list sebelum mengisi data baru
                    for (dataSnapshot in snapshot.children) { // Perbaiki menjadi snapshot.children
                        val pegawai = dataSnapshot.getValue(modelpelanggan::class.java)
                        listPelanggan.add(pegawai!!)

                    }
                    val adapter = adapter_data_pelanggan(listPelanggan)
                    rv_data_pelanggan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error saat gagal mengambil data
                Toast.makeText(this@datapelanggan, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    fun tekan() {
//        tambah2.setOnClickListener {
//            val intent = Intent(this, TambahPelanggan::class.java)
//            startActivity(intent)
//        }
//    }
}
