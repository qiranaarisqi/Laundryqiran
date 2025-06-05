package com.qiranarisqi.laundry.cabang

import adapter.adapter_data_cabang
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
import com.qiranarisqi.laundry.modeldata.modelcabang




class data_cabang : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var rv_data_cabang: RecyclerView
    lateinit var tambah2: FloatingActionButton
    lateinit var listcabang: ArrayList<modelcabang>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rv_data_cabang.layoutManager = layoutManager
        rv_data_cabang.setHasFixedSize(true)

        listcabang = arrayListOf<modelcabang>() // Inisialisasi listPelanggan

        getData()
        tekan()

        // Untuk mengatur padding pada sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rv_data_cabang = findViewById(R.id.rv_data_cabang)
        tambah2 = findViewById(R.id.bt_data_cabang) // Pastikan ini adalah FloatingActionButton
    }

    fun getData() {
        val query = myRef.orderByChild("idcabang").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listcabang.clear() // Bersihkan list sebelum mengisi data baru
                    for (dataSnapshot in snapshot.children) { // Perbaiki menjadi snapshot.children
                        val pegawai = dataSnapshot.getValue(modelcabang::class.java)
                        listcabang.add(pegawai!!)

                    }
                    val adapter = adapter_data_cabang(listcabang)
                    rv_data_cabang.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error saat gagal mengambil data
                Toast.makeText(this@data_cabang, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun tekan() {
        tambah2.setOnClickListener {
            val intent = Intent(this, tambah_cabang::class.java)
            startActivity(intent)
        }
    }
}