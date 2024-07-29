import android.os.Parcel
import android.os.Parcelable

data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var size: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var sellerName: String = "",
    var sellerTell: String = "",
    var sellerPic: String = "",
    var categoryId: Int? = null,
    var id: Int? = null
): Parcelable {

    // Constructor for parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList() as ArrayList<String>,
        parcel.createStringArrayList() as ArrayList<String>,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt()
    )

    // Write object's data to the parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeStringList(picUrl)
        parcel.writeStringList(size)
        parcel.writeDouble(price)
        parcel.writeDouble(rating)
        parcel.writeString(sellerName)
        parcel.writeString(sellerTell)
        parcel.writeString(sellerPic)
        categoryId?.let { parcel.writeInt(it) }
        id?.let { parcel.writeInt(it) }
    }

    // Describe the kinds of special objects contained in this Parcelable instance
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemsModel> {
        // Create a new instance of the Parcelable class, instantiating it from the given Parcel
        override fun createFromParcel(parcel: Parcel): ItemsModel {
            return ItemsModel(parcel)
        }

        // Create a new array of the Parcelable class
        override fun newArray(size: Int): Array<ItemsModel?> {
            return arrayOfNulls(size)
        }
    }
}
