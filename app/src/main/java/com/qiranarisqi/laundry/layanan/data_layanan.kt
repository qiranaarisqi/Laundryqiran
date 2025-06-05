package com.qiranarisqi.laundry.layanan

import adapter.adapter_data_layanan
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
import com.qiranarisqi.laundry.modeldata.modellayanan
import com.qiranarisqi.laundry.R

class data_layanan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var rv_data_layanan: RecyclerView
    lateinit var tambah: FloatingActionButton
    lateinit var listlayanan: ArrayList<modellayanan>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rv_data_layanan.layoutManager = layoutManager
        rv_data_layanan.setHasFixedSize(true)
        listlayanan = arrayListOf<modellayanan>()

        tambah.setOnClickListener {
            val intent = Intent(this, tambah_layanan::class.java)
            startActivity(intent)
        }

        getData()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rv_data_layanan = findViewById(R.id.rv_data_layanan)
        tambah = findViewById(R.id.bt_data_layanan)
    }

    fun getData() {
        val query = myRef.orderByChild("idlayanan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listlayanan.clear() // Bersihkan list sebelum mengisi data baru
                    for (dataSnapshot in snapshot.children) { // Perbaiki menjadi snapshot.children
                        val layanan = dataSnapshot.getValue(modellayanan::class.java)
                        listlayanan.add(layanan!!)

                    }
                    val adapter = adapter_data_layanan(listlayanan)
                    rv_data_layanan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error saat gagal mengambil data
                Toast.makeText(this@data_layanan, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
