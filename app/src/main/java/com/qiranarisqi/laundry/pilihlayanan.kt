package com.qiranarisqi.laundry

import adapter.adapter_pilih_layanan
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.qiranarisqi.laundry.modeldata.modellayanan
import com.qiranarisqi.laundry.modeldata.modeltambahan

class pilihlayanan : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var rvPilihLayanan: RecyclerView
    lateinit var layananList: ArrayList<modellayanan>
    lateinit var tvKosong: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    var idCabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilihlayanan)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", null).toString()

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvPilihLayanan.layoutManager = layoutManager
        rvPilihLayanan.setHasFixedSize(true)

        layananList = arrayListOf()
        getData()

        // SearchView listener
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rvPilihLayanan = findViewById(R.id.rvpilihlayanan)
        tvKosong = findViewById(R.id.tvPILIH_layanan_kosong)
        searchView = findViewById(R.id.searchViewlayanan)
    }

    fun getData() {
        val query = myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layananList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val pelanggan = dataSnapshot.getValue(modellayanan::class.java)
                        if (pelanggan != null) {
                            layananList.add(pelanggan)
                        }
                    }
                    rvPilihLayanan.adapter = adapter_pilih_layanan(layananList)
                    tvKosong.visibility = View.GONE
                } else {
                    tvKosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@pilihlayanan, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun filterList(text: String?) {
        val filteredList = ArrayList<modellayanan>()
        for (item in layananList) {
            if (item.namalayanan?.contains(text ?: "", ignoreCase = true) == true) {
                filteredList.add(item)
            }
        }

        if (filteredList.isNotEmpty()) {
            rvPilihLayanan.adapter = adapter_pilih_layanan(filteredList)
            tvKosong.visibility = View.GONE
        } else {
            rvPilihLayanan.adapter = adapter_pilih_layanan(filteredList)
            tvKosong.visibility = View.VISIBLE
        }
    }
}