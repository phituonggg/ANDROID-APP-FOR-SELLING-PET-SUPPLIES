data class Product(
    val description: String = "",      // Description of the product
    val picUrl: List<String> = listOf(),  // List of URLs for images of the product
    val price: Int = 0,                // Price of the product
    val rating: Double = 0.0,          // Rating of the product
    val sellerName: String = "",       // Name of the seller offering the product
    val sellerPic: String = "",        // URL of the seller's profile picture
    val sellerTell: Long = 0,          // Seller's contact number
    val size: List<String> = listOf(), // List of available sizes for the product
    val title: String = ""             // Title or name of the product
)
