package gabby.paba.myfirstlistview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class KeranjangAdapter(
    private val data: ArrayList<BahanItem>
) : RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onBoughtClicked(data: BahanItem, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class KeranjangViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val _ivBahanImage: ImageView = view.findViewById(R.id.ivBahanImage)
        val _tvBahanNama: TextView = view.findViewById(R.id.tvBahanNama)
        val _tvBahanKategori: TextView = view.findViewById(R.id.tvBahanKategori)
        val _ibSudahBeli: ImageButton = view.findViewById(R.id.ibSudahBeli)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keranjang, parent, false)
        return KeranjangViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeranjangViewHolder, position: Int) {
        val item = data[position]

        holder._tvBahanNama.text = item.nama
        holder._tvBahanKategori.text = item.kategori

        Picasso.get()
            .load(item.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder._ivBahanImage)

        holder._ibSudahBeli.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onItemClickCallback.onBoughtClicked(data[currentPosition], currentPosition)
            }
        }
    }

    override fun getItemCount(): Int = data.size
}