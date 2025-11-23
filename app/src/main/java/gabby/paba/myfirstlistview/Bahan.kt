package gabby.paba.myfirstlistview

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Bahan : AppCompatActivity() {
    var dataBahan = arrayListOf<BahanItem>()
    private lateinit var bahanAdapter: BahanAdapter
    private lateinit var _rvBahan: RecyclerView
    lateinit var sp: SharedPreferences
    private val gson = Gson()
    private val SP_KEY_CART = "dt_cart"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bahan)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_bahan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        sp = getSharedPreferences("data_bahan_sp", MODE_PRIVATE)

        siapkanDataAwal()

        _rvBahan = findViewById(R.id.rvBahan)
        tampilkanData()

        val _btnTambahBahan = findViewById<Button>(R.id.btnTambahBahan)
        _btnTambahBahan.setOnClickListener {
            showAddDialog()
        }

        val rootView = findViewById<LinearLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bahan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> {
                startActivity(Intent(this, Keranjang::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun siapkanDataAwal() {
        val nama = resources.getStringArray(R.array.data_bahan_nama).toMutableList()
        val kategori = resources.getStringArray(R.array.data_bahan_kategori).toMutableList()
        val images = resources.getStringArray(R.array.data_bahan_image).toMutableList()

        dataBahan.clear()
        for (i in nama.indices) {
            dataBahan.add(BahanItem(nama[i], kategori[i], images[i]))
        }
    }

    private fun saveDataToSP(key: String, list: ArrayList<BahanItem>) {
        val json = gson.toJson(list)
        sp.edit {
            putString(key, json)
        }
    }

    private fun loadDataFromSP(key: String): ArrayList<BahanItem> {
        val json = sp.getString(key, null)
        val type = object : TypeToken<ArrayList<BahanItem>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            arrayListOf()
        }
    }

    private fun tampilkanData() {
        _rvBahan.layoutManager = LinearLayoutManager(this)
        bahanAdapter = BahanAdapter(dataBahan)
        _rvBahan.adapter = bahanAdapter

        bahanAdapter.setOnItemClickCallback(object : BahanAdapter.OnItemClickCallback {
            override fun onItemClicked(data: BahanItem) {
                Toast.makeText(this@Bahan, data.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClicked(data: BahanItem, position: Int) {
                showActionDialog(position, data, dataBahan, bahanAdapter)
            }

            override fun onCartClicked(data: BahanItem) {
                val cartList = loadDataFromSP(SP_KEY_CART)
                if (!cartList.contains(data)) {
                    cartList.add(data)
                    saveDataToSP(SP_KEY_CART, cartList)
                    Toast.makeText(this@Bahan, "${data.nama} Ditambah ke Keranjang", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Bahan, "${data.nama} Sudah Ada di Keranjang", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tambah Bahan Baru")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etNewBahan = EditText(this)
        etNewBahan.hint = "Masukkan Nama Bahan"
        layout.addView(etNewBahan)

        val etNewKategori = EditText(this)
        etNewKategori.hint = "Masukkan Kategori Bahan"
        layout.addView(etNewKategori)

        val etNewImage = EditText(this)
        etNewImage.hint = "Masukkan URL Gambar"
        layout.addView(etNewImage)

        builder.setView(layout)

        builder.setPositiveButton("Tambah") { dialog, _ ->
            val namaBahan = etNewBahan.text.toString().trim()
            val kategoriBahan = etNewKategori.text.toString().trim()
            val imageUrl = etNewImage.text.toString().trim()

            if (namaBahan.isNotEmpty() && kategoriBahan.isNotEmpty() && imageUrl.isNotEmpty()) {
                val newItem = BahanItem(namaBahan, kategoriBahan, imageUrl)
                dataBahan.add(newItem)
                bahanAdapter.notifyItemInserted(dataBahan.size - 1)

                Toast.makeText(this, "Bahan $newItem ditambahkan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Semua data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showActionDialog(
        position: Int,
        selectedItem: BahanItem,
        data: ArrayList<BahanItem>,
        adapter: BahanAdapter
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ITEM $selectedItem")
        builder.setMessage("Pilih Tindakan Yang Ingin Dilakukan: ")

        builder.setPositiveButton("Update") { _, _ ->
            showUpdateDialog(position, selectedItem, data, adapter)
        }
        builder.setNegativeButton("Hapus") { _, _ ->
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, data.size)

            Toast.makeText(this, "Hapus Item $selectedItem", Toast.LENGTH_SHORT).show()
        }
        builder.setNeutralButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showUpdateDialog(
        position: Int,
        oldValue: BahanItem,
        data: ArrayList<BahanItem>,
        adapter: BahanAdapter
    ){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Data")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etNewNama = EditText(this)
        etNewNama.setText(oldValue.nama)

        val etNewKategori = EditText(this)
        etNewKategori.setText(oldValue.kategori)

        val etNewImage = EditText(this)
        etNewImage.setText(oldValue.imageUrl)

        layout.addView(etNewNama)
        layout.addView(etNewKategori)
        layout.addView(etNewImage)
        builder.setView(layout)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newNama = etNewNama.text.toString().trim()
            val newKategori = etNewKategori.text.toString().trim()
            val newImage = etNewImage.text.toString().trim()

            if (newNama.isNotEmpty() && newKategori.isNotEmpty() && newImage.isNotEmpty()){
                data[position] = BahanItem(newNama, newKategori, newImage)

                adapter.notifyItemChanged(position)

                Toast.makeText(this, "Data diupdate", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Data baru tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}