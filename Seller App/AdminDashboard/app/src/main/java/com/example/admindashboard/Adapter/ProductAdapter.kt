import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.admindashboard.Activity.EditProductActivity
import com.example.admindashboard.Model.ItemsModel
import com.example.admindashboard.databinding.ViewholderProductBinding

class ProductAdapter(private val productList: List<ItemsModel>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder class for holding the item views
    class ProductViewHolder(val binding: ViewholderProductBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Initialize the click listener for the item
            binding.root.setOnClickListener {
                AlertDialog.Builder(it.context).apply {
                    setTitle("Edit Product")
                    setMessage("Do you want to edit this product?")
                    setPositiveButton("Yes") { _, _ ->
                        // Navigate to EditProductActivity when "Yes" is clicked
                        val intent = Intent(it.context, EditProductActivity::class.java)
                        intent.putExtra("productTitle", binding.titleTxt.text.toString())
                        it.context.startActivity(intent)
                    }
                    setNegativeButton("No", null)
                    show()
                }
            }
        }
    }

    // Create ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Inflate the layout for the ViewHolder
        val binding = ViewholderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        // Set data to the views in the ViewHolder
        holder.binding.titleTxt.text = product.title
        holder.binding.priceTxt.text = product.price.toString()

        // Apply Glide for loading images
        val requestOptions = RequestOptions().transform(CenterCrop())

        Glide.with(holder.binding.imageView.context)
            .load(product.picUrl[0])
            .apply(requestOptions)
            .into(holder.binding.imageView)
    }

    // Return the total number of items in the data set
    override fun getItemCount() = productList.size
}
