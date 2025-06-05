package com.qiranarisqi.laundry.cabang

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
import com.qiranarisqi.laundry.modeldata.modelcabang


class tambah_cabang: AppCompatActivity() {
    val database =  FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var tvtambahcabang:TextView
    lateinit var etnamacabang1:EditText
    lateinit var etalamat :EditText
    lateinit var ettelepon : EditText
    lateinit var etlayanan:EditText
    lateinit var btsimpan : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_cabang)

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
        tvtambahcabang = findViewById(R.id.tvtambahcabang)
        etnamacabang1 = findViewById(R.id.etnamacabang1)
        etalamat = findViewById(R.id.etalamat)
        ettelepon = findViewById(R.id.ettelepon)
        etlayanan = findViewById(R.id. etlayanan)
        btsimpan = findViewById(R.id.btsimpan)

    }

    fun cekValidasi(){
        val nama = etnamacabang1.text.toString()
        val alamat =etalamat.text.toString()
        val noHP = ettelepon.text.toString()
        val layanan =  etlayanan.text.toString()

        if (nama.isEmpty()){
            etnamacabang1.error= this.getString(R.string.validasi_nama)
            Toast.makeText(this, this.getString(R.string.validasi_nama),Toast.LENGTH_SHORT).show()
            etnamacabang1.requestFocus()
            return

        }

        if (alamat.isEmpty()){
            etalamat.error= this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this,this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etalamat.requestFocus()
            return
        }

        if (noHP.isEmpty()){
            ettelepon.error= this.getString(R.string.validasi_nohp)
            Toast.makeText(this,this.getString(R.string.validasi_nohp),Toast.LENGTH_SHORT).show()
            ettelepon.requestFocus()
            return
        }
        if (layanan.isEmpty()){
            etlayanan.error= this.getString(R.string.validasi_cabang)
            Toast.makeText(this,this.getString(R.string.validasi_cabang),Toast.LENGTH_SHORT).show()
            etlayanan.requestFocus()
            return
        }
        simpan()
    }

    fun simpan(){
        val cabangBaru = myRef.push()
        val idcabang = cabangBaru.key
        val data = modelcabang(
            idcabang = toString(),
            etnamacabang1.text.toString(),
            etalamat.text.toString(),
            ettelepon.text.toString(),
            etlayanan.text.toString()
        )
        cabangBaru.setValue(data).addOnSuccessListener {
            Toast.makeText(this,this.getString(R.string.tambah_pegawai_sukses),Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener{
                Toast.makeText(this,this.getString(R.string.tambah_pegawai_gagal),Toast.LENGTH_SHORT).show()
            }
    }
}