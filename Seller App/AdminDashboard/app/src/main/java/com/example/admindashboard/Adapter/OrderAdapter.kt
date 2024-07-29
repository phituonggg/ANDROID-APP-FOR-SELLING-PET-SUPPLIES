import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admindashboard.Model.OrderModel
import com.example.admindashboard.databinding.ViewholderOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderAdapter(private val orders: List<OrderModel>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ViewholderOrderBinding) : RecyclerView.ViewHolder(binding.root)

    // Create ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        // Inflate the layout for the ViewHolder
        val binding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        // Set data to the views in the ViewHolder
        holder.binding.subtotalTxt.text = (order.totalPrice * 0.8).toString()
        holder.binding.totalTxt.text = order.totalPrice.toString()
        holder.binding.orderDateTxt.text = order.orderDate
        holder.binding.statusTxt.text = order.status

        // Handle click listener for the Prepared button
        holder.binding.button2.setOnClickListener {
            // Get reference to the Firebase database
            val ref = FirebaseDatabase.getInstance().getReference("Orders").child(order.orderId)
            // Query for items in the order
            ref.child("items/itemModel").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (itemSnapshot in dataSnapshot.children) {
                        val item = itemSnapshot.getValue(OrderModel.Item::class.java)
                        // If the item matches the first item in the order
                        if (item != null && item.title == order.items[0].title) {
                            // Set isPrepared to true for the item
                            itemSnapshot.ref.child("isPrepared").setValue(true)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    // Return the total number of items in the data set
    override fun getItemCount() = orders.size
}
