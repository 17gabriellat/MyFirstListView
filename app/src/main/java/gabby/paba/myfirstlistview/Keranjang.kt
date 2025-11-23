package gabby.paba.myfirstlistview

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Keranjang : AppCompatActivity() {

    private lateinit var keranjangAdapter: KeranjangAdapter
    private lateinit var rvKeranjang: RecyclerView
    private var cartData = arrayListOf<BahanItem>()
    lateinit var sp: SharedPreferences
    private val gson = Gson()
    private val SP_KEY_CART = "dt_cart"
    private val SP_KEY_BOUGHT = "dt_bought"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_keranjang)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        sp = getSharedPreferences("data_bahan_sp", MODE_PRIVATE)

        cartData = GetDataSP(SP_KEY_CART)

        rvKeranjang = findViewById(R.id.rvKeranjang)
        rvKeranjang.layoutManager = LinearLayoutManager(this)
        keranjangAdapter = KeranjangAdapter(cartData)
        rvKeranjang.adapter = keranjangAdapter

        keranjangAdapter.setOnItemClickCallback(object : KeranjangAdapter.OnItemClickCallback {
            override fun onBoughtClicked(data: BahanItem, position: Int) {
                val boughtList = GetDataSP(SP_KEY_BOUGHT)

                if (!boughtList.contains(data)) {
                    boughtList.add(data)
                    SaveDataSP(SP_KEY_BOUGHT, boughtList)
                }

                cartData.removeAt(position)

                SaveDataSP(SP_KEY_CART, cartData)

                keranjangAdapter.notifyItemRemoved(position)
                keranjangAdapter.notifyItemRangeChanged(position, cartData.size)

                Toast.makeText(this@Keranjang, "${data.nama} Sudah Dibeli", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun SaveDataSP(key: String, list: ArrayList<BahanItem>) {
        val json = gson.toJson(list)
        sp.edit {
            putString(key, json)
        }
    }

    private fun GetDataSP(key: String): ArrayList<BahanItem> {
        val json = sp.getString(key, null)
        val type = object : TypeToken<ArrayList<BahanItem>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            arrayListOf()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}