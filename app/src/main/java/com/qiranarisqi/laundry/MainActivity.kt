package com.qiranarisqi.laundry

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.qiranarisqi.laundry.cabang.data_cabang
import com.qiranarisqi.laundry.layanan.data_layanan

import com.qiranarisqi.laundry.pegawai.data_pegawai
import com.qiranarisqi.laundry.pelanggan.datapelanggan
import com.qiranarisqi.laundry.tambahan.data_tambahan

class MainActivity : AppCompatActivity() {
 lateinit var pegawai1: ImageView
 lateinit var pelanggan1: ImageView
    lateinit var layanan: CardView
    lateinit var cabang: ImageView
    lateinit var tambah : CardView
    lateinit var Transaksi: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        init()
        tekan()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

    }
    fun init(){
        pegawai1 =  findViewById(R.id.ivpegawai)
        pelanggan1 = findViewById(R.id.ivbg2)
        layanan = findViewById(R.id.cv3)
        cabang = findViewById(R.id.ivcabang)
        tambah = findViewById(R.id.cv4)
        Transaksi = findViewById(R.id.ivgb1)
    }

    fun tekan(){
        pegawai1.setOnClickListener {
            val intent =  Intent(this, data_pegawai::class.java)
            startActivity(intent)
        }
        pelanggan1.setOnClickListener {
            val intent = Intent(this, datapelanggan::class.java)
            startActivity(intent)
        }
        layanan.setOnClickListener {
            val intent = Intent(this, data_layanan::class.java)
            startActivity(intent)
        }
        cabang.setOnClickListener {
            val intent = Intent(this, data_cabang::class.java)
            startActivity(intent)
        }
        tambah.setOnClickListener {
            val intent = Intent(this, data_tambahan::class.java)
            startActivity(intent)
        }
        Transaksi.setOnClickListener {
            val intent = Intent(this, transaksi::class.java)
            startActivity(intent)
        }


    }
}