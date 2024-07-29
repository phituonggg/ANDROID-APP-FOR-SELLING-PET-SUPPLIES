import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admindashboard.R

class ImageAdapter(private val context: Context, private val imageUris: List<Uri>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    // Create a ViewHolder for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // Inflate the layout for the ViewHolder
        val view = LayoutInflater.from(context).inflate(R.layout.viewholder_additem_image, parent, false)
        return ImageViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Load the image using Glide library into the ImageView
        Glide.with(context).load(imageUris[position]).into(holder.imageView)
    }

    // Return the total number of items in the data set
    override fun getItemCount() = imageUris.size
}
