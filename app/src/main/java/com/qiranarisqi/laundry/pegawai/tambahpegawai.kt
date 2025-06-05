package com.qiranarisqi.laundry.pegawai

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
import com.qiranarisqi.laundry.modeldata.modelpegawai
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class tambahpegawai: AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")

    lateinit var tvJudul: TextView
    lateinit var etnama: EditText
    lateinit var etlengkap: EditText
    lateinit var etNoHP: EditText
    lateinit var etcab: EditText
    lateinit var btsimpan: Button

    var idPegawai: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tambahpegawai)

        init()
        getData()
        setupClickListeners()
        setupWindowInsets()
    }

    private fun init() {
        tvJudul = findViewById(R.id.tvtambah)
        etnama = findViewById(R.id.etlengkap)
        etlengkap = findViewById(R.id.etallengkap)
        etNoHP = findViewById(R.id.etnohp)
        etcab = findViewById(R.id.etcab)
        btsimpan = findViewById(R.id.btsimpan)
    }

    private fun setupClickListeners() {
        btsimpan.setOnClickListener {
            cekValidasi()
        }
    }

    private fun getData() {
        // ✅ Safe string extraction dengan default values
        idPegawai = intent.getStringExtra("idPegawai") ?: ""
        val judul = intent.getStringExtra("tvjudul") ?: getString(R.string.card_tambah_pegawai)
        val nama = intent.getStringExtra("namaPegawai") ?: ""
        val alamat = intent.getStringExtra("alamatPegawai") ?: ""
        val hp = intent.getStringExtra("noHPPegawai") ?: ""
        val cabang = intent.getStringExtra("tvcabang") ?: ""

        // Set ke UI
        tvJudul.text = judul
        etnama.setText(nama)
        etlengkap.setText(alamat)
        etNoHP.setText(hp)
        etcab.setText(cabang)

        // Logic untuk mode tambah vs edit
        if (judul == getString(R.string.card_tambah_pegawai)) {
            hidup()
            etnama.requestFocus()
            btsimpan.text = "Simpan"
        } else if (judul == "Edit Pegawai") {
            mati()
            btsimpan.text = "Sunting"
        } else {
            hidup()
            etnama.requestFocus()
            btsimpan.text = "Simpan"
        }
    }

    private fun mati() {
        etnama.isEnabled = false
        etlengkap.isEnabled = false
        etNoHP.isEnabled = false
        etcab.isEnabled = false
    }

    private fun hidup() {
        etnama.isEnabled = true
        etlengkap.isEnabled = true
        etNoHP.isEnabled = true
        etcab.isEnabled = true
    }

    private fun update() {
        val pegawaiRef = database.getReference("pegawai").child(idPegawai)
        val data = modelpegawai(
            idPegawai,
            etnama.text.toString(),
            etlengkap.text.toString(),
            etNoHP.text.toString(),
            etcab.text.toString()
        )

        // Buat map untuk update data dengan null safety
        val updateData = mutableMapOf<String, Any>()
        updateData["namaPegawai"] = data.namaPegawai ?: ""
        updateData["alamatPegawai"] = data.alamatPegawai ?: ""
        updateData["noHPPegawai"] = data.noHPPegawai ?: ""
        updateData["tvcabang"] = data.tvcabang ?: ""

        pegawaiRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this@tambahpegawai, "Data Pegawai Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@tambahpegawai, "Data Pegawai Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cekValidasi() {
        val nama = etnama.text.toString().trim()
        val alamat = etlengkap.text.toString().trim()
        val noHP = etNoHP.text.toString().trim()
        val cabang = etcab.text.toString().trim()

        when {
            nama.isEmpty() -> {
                etnama.error = getString(R.string.validasi_nama)
                Toast.makeText(this, getString(R.string.validasi_nama), Toast.LENGTH_SHORT).show()
                etnama.requestFocus()
                return
            }
            alamat.isEmpty() -> {
                etlengkap.error = getString(R.string.validasi_alamat_pegawai)
                Toast.makeText(this, getString(R.string.validasi_alamat_pegawai), Toast.LENGTH_SHORT).show()
                etlengkap.requestFocus()
                return
            }
            noHP.isEmpty() -> {
                etNoHP.error = getString(R.string.validasi_nohp)
                Toast.makeText(this, getString(R.string.validasi_nohp), Toast.LENGTH_SHORT).show()
                etNoHP.requestFocus()
                return
            }
            cabang.isEmpty() -> {
                etcab.error = getString(R.string.validasi_cabang)
                Toast.makeText(this, getString(R.string.validasi_cabang), Toast.LENGTH_SHORT).show()
                etcab.requestFocus()
                return
            }
        }

        when (btsimpan.text.toString()) {
            "Simpan" -> simpan()
            "Sunting" -> {
                hidup()
                etnama.requestFocus()
                btsimpan.text = "Perbarui"
            }
            "Perbarui" -> update()
        }
    }

    private fun simpan() {
        val pegawaiBaru = myRef.push()
        val pegawaiId = pegawaiBaru.key
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val data = modelpegawai(
            pegawaiId ?: "",
            etnama.text.toString().trim(),
            etlengkap.text.toString().trim(),
            etNoHP.text.toString().trim(),
            etcab.text.toString().trim(),
            currentTime
        )

        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.tambah_pegawai_sukses), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.tambah_pegawai_gagal), Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}