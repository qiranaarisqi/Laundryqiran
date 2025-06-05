package com.qiranarisqi.laundry.layanan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modellayanan

class tambah_layanan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var etnamalayanan: EditText
    lateinit var etharga: EditText
    lateinit var etnamacabang: EditText
    lateinit var btsimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tambah_layanan)
        init()
        btsimpan.setOnClickListener {
            cekValidasi()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init() {
        etnamalayanan = findViewById(R.id.etnamalayanan)
        etharga = findViewById(R.id.etharga)
        etnamacabang = findViewById(R.id.etnamacabang)
        btsimpan = findViewById(R.id.btsimpanlayanan)
    }

    fun cekValidasi() {
        val namalayanan = etnamalayanan.text.toString()
        val harga = etharga.text.toString()
        val namacabang = etnamacabang.text.toString()

        //validasi data
        if (namalayanan.isEmpty()) {
            etnamalayanan.error = this.getString(R.string.validasi_nama_layanan)
            Toast.makeText(
                this@tambah_layanan,
                this.getString(R.string.validasi_nama_layanan),
                Toast.LENGTH_SHORT
            ).show()
            etnamalayanan.requestFocus()
            return
        }
        if (harga.isEmpty()) {
            etharga.error = this.getString(R.string.validasi_harga_layanan)
            Toast.makeText(
                this@tambah_layanan,
                this.getString(R.string.validasi_harga_layanan),
                Toast.LENGTH_SHORT
            ).show()
            etharga.requestFocus()
            return
        }
        if (namacabang.isEmpty()) {
            etnamacabang.error = this.getString(R.string.validasi_nama_cabang)
            Toast.makeText(
                this@tambah_layanan,
                this.getString(R.string.validasi_nama_cabang),
                Toast.LENGTH_SHORT
            ).show()
            etnamacabang.requestFocus()
            return
        }

        simpan()


    }
    fun simpan() {
        val layananBaru = myRef.push()
        val layananId = layananBaru.key ?: return
        val data = modellayanan(
            idlayanan = layananId,
            namalayanan = etnamalayanan.text.toString(),
            harga = etharga.text.toString(),
            namacabang = etnamacabang.text.toString()

        )
        layananBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.tambah_layanan_simpan), Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("idlayanan", layananId)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.tambah_layanan_gagal), Toast.LENGTH_SHORT).show()
            }


    }


}