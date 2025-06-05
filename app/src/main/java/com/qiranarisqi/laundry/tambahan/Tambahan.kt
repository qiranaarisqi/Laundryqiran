package com.qiranarisqi.laundry.tambahan

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
import com.qiranarisqi.laundry.modeldata.modeltambahan

class Tambahan : AppCompatActivity() {
    val database =  FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var tvJudul:TextView
    lateinit var etnama:EditText
    lateinit var etharga :EditText
    lateinit var etcabang : EditText
    lateinit var etstatus:EditText
    lateinit var btsimpan : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambahan)

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
        tvJudul = findViewById(R.id.tvtambah_tambahan)
        etnama = findViewById(R.id.etnamatambahan)
        etharga = findViewById(R.id.etharga)
        etcabang = findViewById(R.id.etcabangtambahan)
        etstatus = findViewById(R.id.etstatustambahan)
        btsimpan = findViewById(R.id.btsimpantambahan)

    }

    fun cekValidasi(){
        val nama = etnama.text.toString()
        val harga = etharga.text.toString()
        val cabang = etcabang.text.toString()
        val status =  etstatus.text.toString()

        if (nama.isEmpty()){
            etnama.error= this.getString(R.string.validasi_nama_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_tambahan),Toast.LENGTH_SHORT).show()
            etnama.requestFocus()
            return

        }

        if (harga.isEmpty()){
            etharga.error= this.getString(R.string.validasi_harga_tambahan)
            Toast.makeText(this,this.getString(R.string.validasi_harga_tambahan),Toast.LENGTH_SHORT).show()
            etharga.requestFocus()
            return
        }

        if (cabang.isEmpty()){
            etcabang.error= this.getString(R.string.validasi_cabang_tambahan)
            Toast.makeText(this,this.getString(R.string.validasi_cabang_tambahan),Toast.LENGTH_SHORT).show()
            etcabang.requestFocus()
            return
        }
        if (status.isEmpty()){
            etstatus.error= this.getString(R.string.validasi_status_tambahan)
            Toast.makeText(this,this.getString(R.string.validasi_status_tambahan),Toast.LENGTH_SHORT).show()
            etstatus.requestFocus()
            return
        }
        simpan()
    }

    fun simpan(){
        val tambahanBaru = myRef.push()
        val tambahanId = tambahanBaru.key
        val data = modeltambahan(
            tambahanId.toString(),
            etnama.text.toString(),
            etharga.text.toString(),
            etcabang.text.toString(),
            etstatus.text.toString()
        )
       tambahanBaru.setValue(data).addOnSuccessListener {
            Toast.makeText(this,this.getString(R.string.tambahan_sukses),Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener{
                Toast.makeText(this,this.getString(R.string.tambahan_gagal),Toast.LENGTH_SHORT).show()
            }
    }
}