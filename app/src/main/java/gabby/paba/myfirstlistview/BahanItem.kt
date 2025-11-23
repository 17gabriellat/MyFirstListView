package gabby.paba.myfirstlistview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class BahanItem(
    var nama: String,
    var kategori: String,
    var imageUrl: String
) : Parcelable {


    override fun toString(): String {
        return "$nama ($kategori)"
    }
}