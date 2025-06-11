package com.qiranarisqi.laundry

import adapter.AdapterTambahanDipilih
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiranarisqi.laundry.modeldata.modeltambahan // Menggunakan modeltambahan yang Parcelable
import com.qiranarisqi.laundry.modeldata.modeltransaksitambahan // Import modeltransaksitambahan
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class pembayaran_akhir : AppCompatActivity() {

    // Deklarasi TextViews dan Buttons
    private lateinit var tvTanggal: TextView
    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvNamaPelanggan: TextView // Telah diinisialisasi// Telah diinisialisasi
    private lateinit var tvLayananUtama: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvTambahan: RecyclerView
    private lateinit var tvSubtotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnCetak: Button
    private lateinit var btnKirimWhatsapp: Button

    // PERBAIKAN: List ini sekarang bertipe MutableList<modeltransaksitambahan>
    // karena ini adalah tipe yang diharapkan oleh AdapterTambahanDipilih.
    private val listTambahanUntukAdapter: MutableList<modeltransaksitambahan> = mutableListOf()
    private var adapter: AdapterTambahanDipilih? = null

    private var noHPPelanggan: String = "" // Menyimpan nomor HP pelanggan untuk WhatsApp

    // Variabel Bluetooth
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // Ganti dengan MAC Address printer Bluetooth Anda yang sebenarnya
    private val printerMAC = "DC:0D:51:A7:FF:7A" // Contoh MAC Address, ganti dengan MAC printer Anda
    private val printerUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") // UUID standar untuk SPP

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 100
        private const val TAG = "InvoiceTransaksi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pembayaran_akhir)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        debugIntentExtras() // Debug semua extra yang diterima (hanya untuk pengembangan)

        initializeViews()        // Inisialisasi elemen UI
        initializeBluetooth()    // Inisialisasi pengaturan Bluetooth
        requestBluetoothPermissions() // Meminta izin Bluetooth jika belum ada
        setupRecyclerView()      // Mengatur RecyclerView untuk layanan tambahan
        loadDataFromIntent()     // Memuat data transaksi dari Intent
        setupButtons()           // Mengatur listener untuk tombol-tombol

        setupActionBar()         // Mengatur ActionBar
        setupBackPressHandler()  // Mengatur aksi ketika tombol kembali ditekan
    }

    // Mengatur aksi ketika tombol kembali ditekan
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToDataTransaksi() // Kembali ke Activity utama
            }
        })
    }

    // Mengatur ActionBar (judul dan tombol kembali)
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Invoice Transaksi"
    }

    // Aksi ketika tombol panah kembali di ActionBar ditekan
    override fun onSupportNavigateUp(): Boolean {
        navigateBackToDataTransaksi() // Kembali ke Activity utama
        return true
    }

    // Navigasi kembali ke Activity utama (MainActivity)
    private fun navigateBackToDataTransaksi() {
        try {
            // Pastikan Anda memiliki Activity bernama MainActivity di package com.qiranisqi.laundry
            val intent = Intent(this, com.qiranarisqi.laundry.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Menutup Activity saat ini
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating back to Home", e)
            showToast("Tidak dapat kembali ke halaman utama.") // Memberi tahu pengguna jika ada error
            finish() // Fallback: tutup activity ini
        }
    }

    // Fungsi untuk menampilkan semua extra yang diterima Intent di Logcat
    private fun debugIntentExtras() {
        val extras = intent.extras
        if (extras != null) {
            Log.d(TAG, "=== DEBUG INTENT EXTRAS ===")
            for (key in extras.keySet()) {
                val value = extras.get(key)
                Log.d(TAG, "Key: $key = Value: $value")
            }
            Log.d(TAG, "=== END DEBUG EXTRAS ===")
        } else {
            Log.d(TAG, "No extras found in intent")
        }
    }

    // Fungsi untuk menginisialisasi semua elemen UI (TextViews, Buttons, RecyclerView)
    private fun initializeViews() {
        try {
            tvTanggal = findViewById(R.id.waktupembayaranPA)
            tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
            tvIdTransaksi = findViewById(R.id.idpembayaranakhir)
            tvLayananUtama = findViewById(R.id.pesananpembayaranakhir)
            tvHargaLayanan = findViewById(R.id.hargalayananPA)
            rvTambahan = findViewById(R.id.rvpembayaranakhir) // Pastikan ID ini ada di layout XML
            tvSubtotalTambahan = findViewById(R.id.subtotal)
            tvTotalBayar = findViewById(R.id.subtotalharga)
            btnCetak = findViewById(R.id.cetak)
            btnKirimWhatsapp = findViewById(R.id.kirimWA)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            showToast("Error inisialisasi tampilan: ${e.message}. Pastikan semua ID TextView/Button ada di layout Anda.")
            finish() // Menutup Activity jika inisialisasi gagal
        }
    }

    // Fungsi untuk menginisialisasi BluetoothAdapter
    private fun initializeBluetooth() {
        try {
            bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothManager.adapter
            } else {
                @Suppress("DEPRECATION")
                BluetoothAdapter.getDefaultAdapter()
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException initializing Bluetooth", e)
            showToast("Tidak dapat mengakses Bluetooth: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Bluetooth", e)
            showToast("Error inisialisasi Bluetooth: ${e.message}")
        }
    }

    // Memeriksa apakah semua izin Bluetooth yang diperlukan sudah diberikan
    private fun hasAllBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 (S) dan di atasnya
            val connectPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            val scanPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            connectPermission && scanPermission
        } else { // Android 11 (R) dan di bawahnya
            val bluetoothPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
            val bluetoothAdminPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
            bluetoothPermission && bluetoothAdminPermission
        }
    }

    // Memeriksa izin BLUETOOTH_CONNECT (untuk Android 12+) atau BLUETOOTH (untuk Android < 12)
    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Meminta izin Bluetooth dari pengguna
    private fun requestBluetoothPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                REQUEST_BLUETOOTH_PERMISSIONS
            )
        }
    }

    // Menampilkan pesan Toast dan mengarahkan ke pengaturan aplikasi jika izin tidak diberikan
    private fun showPermissionSettingsPrompt() {
        showToast("Harap aktifkan izin Bluetooth di pengaturan aplikasi")
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }

    // Mengatur RecyclerView dengan adapter
    private fun setupRecyclerView() {
        // PERBAIKAN: Menggunakan listTambahanUntukAdapter yang bertipe MutableList<modeltransaksitambahan>
        adapter = AdapterTambahanDipilih(listTambahanUntukAdapter)
        rvTambahan.layoutManager = LinearLayoutManager(this)
        rvTambahan.adapter = adapter
    }

    // Memuat data dari Intent yang diterima
    private fun loadDataFromIntent() {
        try {
            Log.d(TAG, "Loading data from individual extras (from konfirmasidata)")
            loadFromIndividualData() // Memuat data dari extra individual

        } catch (e: Exception) {
            Log.e(TAG, "Error loading data from intent", e)
            showToast("Error memuat data: ${e.message}")
            finish()
        }
    }

    // Memuat data dari individual extras yang dikirim dari konfirmasidata
    private fun loadFromIndividualData() {
        val idTransaksi = intent.getStringExtra("idTransaksi") ?: "TRX${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())}"
        val nama = intent.getStringExtra("nama") ?: "-"
        noHPPelanggan = intent.getStringExtra("noHp") ?: "" // Mengambil noHp
        val layanan = intent.getStringExtra("layanan") ?: "-"
        val hargaLayananInt = intent.getIntExtra("hargaLayanan", 0) // Mengambil harga layanan utama sebagai Int
        val subtotalTambahanInt = intent.getIntExtra("subtotalTambahan", 0) // Mengambil subtotal tambahan sebagai Int
        val totalBayarInt = intent.getIntExtra("totalBayar", 0) // Mengambil total bayar sebagai Int
        val metodePembayaran = intent.getStringExtra("metodePembayaran") ?: "-"
        val tanggalPembayaran = intent.getStringExtra("tanggalPembayaran") ?: "-"
        val waktuPembayaran = intent.getStringExtra("waktuPembayaran") ?: "-"

        // Mengambil list layanan tambahan sebagai ParcelableArrayList (modeltambahan)
        val tambahanModeltambahan: ArrayList<modeltambahan> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("layananTambahanList", modeltambahan::class.java) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<modeltambahan>("layananTambahanList") ?: arrayListOf()
        }

        // PERBAIKAN UTAMA: Mengkonversi ArrayList<modeltambahan> ke MutableList<modeltransaksitambahan>
        listTambahanUntukAdapter.clear() // Bersihkan list yang akan digunakan adapter
        tambahanModeltambahan.forEach { itemModeltambahan ->
            // Membuat objek modeltransaksitambahan dari properti modeltambahan
            listTambahanUntukAdapter.add(
                modeltransaksitambahan(
                    itemModeltambahan.idtambahan, // idtambahan dari modeltambahan
                    itemModeltambahan.namatambahan, // namatambahan dari modeltambahan
                    itemModeltambahan.hargatambahan // hargatambahan dari modeltambahan
                )
            )
        }

        // --- Tampilkan Data ke UI ---
        tvTanggal.text = "$tanggalPembayaran $waktuPembayaran"
        tvIdTransaksi.text = idTransaksi
        tvNamaPelanggan.text = nama // Menampilkan nama pelanggan di UI


        tvLayananUtama.text = layanan
        tvHargaLayanan.text = formatCurrency(hargaLayananInt)

        // Beri tahu adapter bahwa data telah berubah
        adapter?.notifyDataSetChanged()

        tvSubtotalTambahan.text = formatCurrency(subtotalTambahanInt)
        tvTotalBayar.text = formatCurrency(totalBayarInt)
    }

    // Fungsi untuk memformat timestamp (Long) ke string tanggal dan waktu
    private fun formatTimestamp(timestamp: Long?): String {
        return try {
            if (timestamp == null || timestamp == 0L) return "-"
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting timestamp", e)
            "-"
        }
    }

    // Fungsi untuk memformat nilai integer ke format mata uang Rupiah
    private fun formatCurrency(value: Int): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            formatter.maximumFractionDigits = 0 // Tidak menampilkan desimal
            return formatter.format(value)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting currency", e)
            "Rp $value" // Fallback jika format gagal
        }
    }

    // Mengatur listener untuk tombol Cetak dan Kirim WhatsApp
    @SuppressLint("MissingPermission") // Suppress karena permission dicek di hasAllBluetoothPermissions()
    private fun setupButtons() {
        btnCetak.setOnClickListener {
            if (!hasAllBluetoothPermissions()) {
                showToast("Izin Bluetooth diperlukan untuk mencetak")
                requestBluetoothPermissions() // Meminta izin
                return@setOnClickListener
            }
            val message = buildPrintMessage() // Membangun pesan untuk dicetak
            printToBluetooth(message) // Mencetak melalui Bluetooth
        }

        btnKirimWhatsapp.setOnClickListener {
            Log.d(TAG, "WhatsApp button clicked. noHPPelanggan = '$noHPPelanggan'")
            // PERBAIKAN: Gunakan noHPPelanggan yang sudah diinisialisasi dari Intent
            if (noHPPelanggan.isEmpty() || noHPPelanggan.isBlank() || noHPPelanggan == "Tidak tersedia") {
                showToast("Nomor HP pelanggan tidak tersedia atau tidak valid untuk WhatsApp.")
                Log.w(TAG, "WhatsApp failed: No valid phone number. Value: '$noHPPelanggan'")
                return@setOnClickListener
            }
            val message = buildWhatsappMessage() // Membangun pesan untuk WhatsApp
            sendWhatsappMessage(message) // Mengirim pesan WhatsApp
        }
    }

    // Menentukan status pembayaran (Lunas, Belum Lunas, dll.)
    private fun determinePaymentStatus(statusFromIntent: String?, methodFromIntent: String?): String {
        val finalStatus = statusFromIntent ?: "Belum Lunas"
        val finalMethod = methodFromIntent ?: ""

        return when {
            finalStatus.contains("Selesai", ignoreCase = true) -> "Lunas"
            finalStatus.contains("Lunas", ignoreCase = true) -> "Lunas"
            finalStatus.contains("Paid", ignoreCase = true) -> "Lunas"
            finalStatus.contains("Proses", ignoreCase = true) -> "Dalam Proses"
            finalStatus.contains("Menunggu", ignoreCase = true) -> "Belum Lunas ($finalMethod)"
            finalStatus.contains("Pending", ignoreCase = true) -> "Belum Lunas ($finalMethod)"
            else -> finalStatus // Default ke status yang diterima
        }
    }

    // Membangun pesan invoice untuk dicetak oleh printer Bluetooth
    private fun buildPrintMessage(): String {
        return buildString {
            append("\n")
            append("Laundry Qirana\n")
            append("Alamat Laundry Anda\n") // Ganti dengan alamat laundry yang sebenarnya
            append("==============================\n")
            append("ID Transaksi: ${tvIdTransaksi.text}\n")
            append("Tanggal: ${tvTanggal.text}\n")
            append("Pelanggan: ${tvNamaPelanggan.text}\n") // Menggunakan tvNamaPelanggan


            val namaUtama = tvLayananUtama.text.toString().take(20).padEnd(20)
            val hargaUtama = tvHargaLayanan.text.toString().padStart(12)
            append("$namaUtama$hargaUtama\n")

            if (listTambahanUntukAdapter.isNotEmpty()) { // Menggunakan listTambahanUntukAdapter
                append("\nLayanan Tambahan:\n")
                listTambahanUntukAdapter.forEachIndexed { index, item ->
                    val nama = "${index + 1}. ${item.nama ?: ""}".take(20).padEnd(20) // Menggunakan item.nama
                    val harga = (item.harga ?: "0").padStart(12) // Menggunakan item.harga
                    append("$nama Rp $harga\n")
                }
                append("------------------------------\n")
                append("Subtotal Tambahan: ${tvSubtotalTambahan.text}\n")
            }

            append("TOTAL BAYAR: ${tvTotalBayar.text}\n")
            append("==============================\n")
            append("Terima kasih telah memilih\n")
            append("Laundry Elnoah\n")
            append("\n\n\n") // Spasi kosong untuk memajukan kertas
        }
    }

    // Membangun pesan invoice untuk dikirim melalui WhatsApp
    private fun buildWhatsappMessage(): String {
        return buildString {
            append("*Hai ${tvNamaPelanggan.text}* 👋\n\n") // Menggunakan tvNamaPelanggan
            append("*Berikut rincian laundry Anda:*\n")
            append("• ID Transaksi: ${tvIdTransaksi.text}\n")
            append("• Tanggal: ${tvTanggal.text}\n")
            append("\n")
            append("*Layanan Utama:*\n")
            append("• ${tvLayananUtama.text} - ${tvHargaLayanan.text}\n\n")

            if (listTambahanUntukAdapter.isNotEmpty()) { // Menggunakan listTambahanUntukAdapter
                append("*Layanan Tambahan:*\n")
                listTambahanUntukAdapter.forEachIndexed { index, item ->
                    append("${index + 1}. ${item.nama ?: ""} - Rp ${item.harga ?: "0"}\n") // Menggunakan item.nama dan item.harga
                }
                append("\n")
            }

            append("*Total Bayar:* ${tvTotalBayar.text}\n\n")
            append("Terima kasih telah menggunakan Laundry Elnoah 💙")
        }
    }

    // Mengirim pesan WhatsApp ke nomor pelanggan
    private fun sendWhatsappMessage(message: String) {
        try {
            // PERBAIKAN: Inisialisasi formattedNumber di sini
            val formattedNumber = formatPhoneNumber(noHPPelanggan) // BARIS INI HARUS ADA!
            Log.d(TAG, "Original number for WhatsApp: '$noHPPelanggan', Formatted: '$formattedNumber'")

            if (formattedNumber.isEmpty()) {
                showToast("Nomor HP tidak valid atau tidak dapat diformat.")
                return
            }

            // Mencoba menggunakan skema wa.me
            val whatsappUrl = "https://wa.me/$formattedNumber?text=${Uri.encode(message)}"
            Log.d(TAG, "WhatsApp URL: $whatsappUrl")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
            startActivity(intent)
            showToast("Membuka WhatsApp...")

        } catch (e: Exception) {
            Log.e(TAG, "Error mengirim pesan WhatsApp", e)
            // Fallback jika wa.me tidak berfungsi (misalnya jika WhatsApp tidak terinstal)
            try {
                // PERBAIKAN: Inisialisasi formattedNumber di sini juga untuk fallback
                val formattedNumber = formatPhoneNumber(noHPPelanggan) // BARIS INI JUGA!
                val fallbackUrl = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                startActivity(fallbackIntent)
                showToast("Membuka WhatsApp...")
            } catch (e2: Exception) {
                showToast("Tidak dapat membuka WhatsApp.")
                Log.e(TAG, "Error fallback WhatsApp", e2)
            }
        }
    }

    // Memformat nomor telepon agar sesuai dengan format WhatsApp (misalnya 628xxxx)
    private fun formatPhoneNumber(phoneNumber: String): String {
        Log.d(TAG, "formatPhoneNumber called with: '$phoneNumber'")
        if (phoneNumber.isEmpty() || phoneNumber.isBlank() || phoneNumber == "Tidak tersedia") {
            Log.w(TAG, "Phone number is empty, blank, or 'Tidak tersedia'.")
            return ""
        }

        // Hapus semua karakter non-digit
        var cleanedNumber = phoneNumber.replace("[^\\d]".toRegex(), "")

        // Jika dimulai dengan '0', ganti dengan '62'
        if (cleanedNumber.startsWith("0")) {
            cleanedNumber = "62" + cleanedNumber.substring(1)
        }
        // Jika belum dimulai dengan '62', tambahkan '62' di depannya
        else if (!cleanedNumber.startsWith("62")) {
            cleanedNumber = "62" + cleanedNumber
        }

        Log.d(TAG, "Formatted phone number: '$cleanedNumber'")
        return cleanedNumber
    }

    // --- Fungsi Bluetooth untuk Pencetakan ---

    // Menghubungkan ke printer Bluetooth
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT) // Perbaiki Manifest. চলছিল menjadi Manifest.permission
    private fun connectBluetoothDevice() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            showToast("Bluetooth tidak tersedia atau tidak diaktifkan.")
            return
        }

        if (!hasBluetoothConnectPermission()) {
            showToast("Izin Bluetooth Connect diperlukan.")
            requestBluetoothPermissions()
            return
        }

        showToast("Menghubungkan ke printer...")
        Thread {
            try {
                val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(printerMAC)
                if (device == null) {
                    runOnUiThread { showToast("Printer tidak ditemukan.") }
                    return@Thread
                }

                bluetoothSocket = device.createRfcommSocketToServiceRecord(printerUUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
                runOnUiThread { showToast("Terhubung ke printer.") }
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException saat menghubungkan Bluetooth", e)
                runOnUiThread { showToast("Gagal terhubung: Izin Bluetooth tidak diberikan.") }
                showPermissionSettingsPrompt() // Minta izin lagi jika ada SecurityException
            } catch (e: Exception) {
                Log.e(TAG, "Error menghubungkan Bluetooth", e)
                runOnUiThread { showToast("Gagal terhubung ke printer: ${e.message}") }
                closeBluetoothSocket()
            }
        }.start()
    }

    // Mengirim pesan ke printer Bluetooth
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun printToBluetooth(message: String) {
        if (outputStream == null) {
            // Coba sambungkan jika belum terhubung
            connectBluetoothDevice()
            // Beri sedikit waktu untuk koneksi, lalu coba cetak lagi
            Handler(Looper.getMainLooper()).postDelayed({
                if (outputStream != null) {
                    try {
                        outputStream?.write(message.toByteArray())
                        runOnUiThread { showToast("Invoice berhasil dicetak!") }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error saat mencetak", e)
                        runOnUiThread { showToast("Gagal mencetak: ${e.message}") }
                        closeBluetoothSocket()
                    }
                } else {
                    runOnUiThread { showToast("Tidak dapat mencetak: Printer tidak terhubung.") }
                }
            }, 2000) // Delay 2 detik
        } else {
            try {
                outputStream?.write(message.toByteArray())
                runOnUiThread { showToast("Invoice berhasil dicetak!") }
            } catch (e: Exception) {
                Log.e(TAG, "Error saat mencetak (sudah terhubung)", e)
                runOnUiThread { showToast("Gagal mencetak: ${e.message}") }
                closeBluetoothSocket()
            }
        }
    }

    // Menutup socket Bluetooth
    private fun closeBluetoothSocket() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
            outputStream = null
            bluetoothSocket = null
            Log.d(TAG, "Bluetooth socket ditutup.")
        } catch (e: Exception) {
            Log.e(TAG, "Error menutup Bluetooth socket", e)
        }
    }

    // Callback saat izin Bluetooth diminta
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (hasAllBluetoothPermissions()) {
                showToast("Izin Bluetooth diberikan!")
            } else {
                showToast("Beberapa izin Bluetooth ditolak. Fungsi cetak mungkin tidak berfungsi.")
                // Opsional: tampilkan dialog untuk mengarahkan pengguna ke pengaturan aplikasi
            }
        }
    }

    // Fungsi helper untuk menampilkan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeBluetoothSocket() // Pastikan Bluetooth socket ditutup saat Activity dihancurkan
    }
}
