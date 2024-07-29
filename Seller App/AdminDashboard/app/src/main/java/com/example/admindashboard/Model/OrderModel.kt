data class OrderModel(
    val orderId: String = "",       // Unique identifier for the order
    val userId: String = "",        // Identifier for the user who placed the order
    val items: List<Item> = listOf(),  // List of items in the order
    val totalQuantity: Int = 0,     // Total quantity of items in the order
    val totalPrice: Double = 0.0,   // Total price of the order
    val orderDate: String = "",     // Date and time when the order was placed
    val status: String = "",        // Current status of the order
    var subtotal: Double = 0.0      // Subtotal of the order (added in this line)
) {
    data class Item(
        val title: String = "",      // Title of the item
        var quantity: Int = 0,       // Quantity of this item in the order
        val price: Double = 0.0,     // Price per unit of the item
        val itemModel: ItemsModel? = null  // Reference to the ItemsModel of this item (if available)
    )
}
