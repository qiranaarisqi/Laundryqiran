package com.qiranarisqi.laundry

import adapter.AdapterTambahanDipilih
import adapter.Adapterlayanantambahan
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
import com.qiranarisqi.laundry.modeldata.modeltransaksitambahan

class data_tambahan_item :  AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var rvpilihtambahan: RecyclerView
    lateinit var layananList: ArrayList<modeltambahan>
    lateinit var tvKosong: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    var idCabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_tambahan_item)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", null).toString()

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvpilihtambahan.layoutManager = layoutManager
        rvpilihtambahan.setHasFixedSize(true)

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
        rvpilihtambahan = findViewById(R.id.rvpilihtambahan)
        tvKosong = findViewById(R.id.tvPILIH_tambahan_kosong)
        searchView = findViewById(R.id.searchViewitemtambahan)
    }

    fun getData() {
        val query = myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layananList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val pelanggan = dataSnapshot.getValue(modeltambahan::class.java)
                        if (pelanggan != null) {
                            layananList.add(pelanggan)
                        }
                    }
                    // Cek apakah ini pemilihan tambahan atau layanan utama
                    val isTambahan = intent.getBooleanExtra("isTambahan", false)
                    rvpilihtambahan.adapter = Adapterlayanantambahan(layananList)

                    tvKosong.visibility = View.GONE
                } else {
                    tvKosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@data_tambahan_item, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun filterList(text: String?) {
        val filteredList = ArrayList<modeltambahan>()
        for (item in layananList) {
            if (item.namatambahan?.contains(text ?: "", ignoreCase = true) == true) {
                filteredList.add(item)
            }
        }

        if (filteredList.isNotEmpty()) {
            rvpilihtambahan.adapter = Adapterlayanantambahan(filteredList)
            tvKosong.visibility = View.GONE
        } else {
            rvpilihtambahan.adapter = Adapterlayanantambahan(filteredList)
            tvKosong.visibility = View.VISIBLE
        }
    }
}