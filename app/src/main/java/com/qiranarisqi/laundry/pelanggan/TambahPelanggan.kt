package com.qiranarisqi.laundry.pelanggan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.qiranarisqi.laundry.R
import com.qiranarisqi.laundry.modeldata.modelpelanggan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TambahPelanggan : AppCompatActivity() {
    val database =  FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var tvJudul:TextView
    lateinit var etnama:EditText
    lateinit var etlengkap :EditText
    lateinit var etNoHP : EditText
    lateinit var btsimpan : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pelanggan)

        init()
        btsimpan.setOnClickListener{
            cekValidasi()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        tvJudul = findViewById(R.id.tvtambah1)
        etnama = findViewById(R.id.etlengkap1)
        etlengkap = findViewById(R.id.etallengkap1)
        etNoHP = findViewById(R.id.etnohp1)
        btsimpan = findViewById(R.id.btsimpan1)

    }

    fun cekValidasi(){
        val nama = etnama.text.toString()
        val alamat = etlengkap.text.toString()
        val noHP = etNoHP.text.toString()

        if (nama.isEmpty()){
            etnama.error= this.getString(R.string.validasi_nama)
            Toast.makeText(this, this.getString(R.string.validasi_nama),Toast.LENGTH_SHORT).show()
            etnama.requestFocus()
            return

        }

        if (alamat.isEmpty()){
            etlengkap.error= this.getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(this,this.getString(R.string.validasi_alamat_pelanggan),Toast.LENGTH_SHORT).show()
            etlengkap.requestFocus()
            return
        }

        if (noHP.isEmpty()){
            etNoHP.error= this.getString(R.string.validasi_nohp)
            Toast.makeText(this,this.getString(R.string.validasi_nohp),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }

        simpan()
    }

    fun simpan(){
        val pelangganBaru = myRef.push()
        val pelangganId = pelangganBaru.key
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val data = modelpelanggan(
            pelangganId.toString(),
            etnama.text.toString(),
            etlengkap.text.toString(),
            etNoHP.text.toString(),
            currentTime
        )
        pelangganBaru.setValue(data).addOnSuccessListener {
            Toast.makeText(this,this.getString(R.string.tambah_pelanggan_sukses),Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener{
                Toast.makeText(this,this.getString(R.string.tambah_pelanggan_gagal),Toast.LENGTH_SHORT).show()
            }
    }
}