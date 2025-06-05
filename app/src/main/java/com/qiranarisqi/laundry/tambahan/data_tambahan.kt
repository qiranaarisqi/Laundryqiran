package com.qiranarisqi.laundry.tambahan

import adapter.adapter_data_tambahan
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
import com.qiranarisqi.laundry.modeldata.modeltambahan
import com.qiranarisqi.laundry.pegawai.tambahpegawai


class data_tambahan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var rv_data_tambahan: RecyclerView
    lateinit var tambah2: FloatingActionButton
    lateinit var listtambahan: ArrayList<modeltambahan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_tambahan)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rv_data_tambahan.layoutManager = layoutManager
        rv_data_tambahan.setHasFixedSize(true)

        listtambahan = arrayListOf<modeltambahan>() // Inisialisasi listTambahan

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
        rv_data_tambahan = findViewById(R.id.rv_data_tambahan)
        tambah2 = findViewById(R.id.bt_data_tambahan) // Pastikan ini adalah FloatingActionButton
    }

    fun getData() {
        val query = myRef.orderByChild("idtambahan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listtambahan.clear() // Bersihkan list sebelum mengisi data baru
                    for (dataSnapshot in snapshot.children) { // Perbaiki menjadi snapshot.children
                        val tambahan = dataSnapshot.getValue(modeltambahan::class.java)
                        listtambahan.add(tambahan!!)

                    }
                    val adapter = adapter_data_tambahan(listtambahan)
                    rv_data_tambahan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error saat gagal mengambil data
                Toast.makeText(this@data_tambahan, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun tekan() {
        tambah2.setOnClickListener {
            val intent = Intent(this, Tambahan::class.java)
            startActivity(intent)
        }
    }
}