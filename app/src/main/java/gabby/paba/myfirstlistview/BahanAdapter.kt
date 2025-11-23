package gabby.paba.myfirstlistview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BahanAdapter(private val data: ArrayList<BahanItem>) :
    RecyclerView.Adapter<BahanAdapter.BahanViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: BahanItem)
        fun onItemLongClicked(data: BahanItem, position: Int)
        fun onCartClicked(data: BahanItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class BahanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val _ivBahanImage: ImageView = view.findViewById(R.id.ivBahanImage)
        val _tvBahanNama: TextView = view.findViewById(R.id.tvBahanNama)
        val _tvBahanKategori: TextView = view.findViewById(R.id.tvBahanKategori)
        val _ibAddToCart: ImageButton = view.findViewById(R.id.ibAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BahanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bahan, parent, false)
        return BahanViewHolder(view)
    }

    override fun onBindViewHolder(holder: BahanViewHolder, position: Int) {
        val item = data[position]

        holder._tvBahanNama.text = item.nama
        holder._tvBahanKategori.text = item.kategori

        Picasso.get()
            .load(item.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder._ivBahanImage)

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onItemClickCallback.onItemClicked(data[currentPosition])
            }
        }

        holder.itemView.setOnLongClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onItemClickCallback.onItemLongClicked(data[currentPosition], currentPosition)
            }
            true
        }

        holder._ibAddToCart.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onItemClickCallback.onCartClicked(data[currentPosition])
            }
        }
    }

    override fun getItemCount(): Int = data.size
}