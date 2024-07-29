data class SellerModel(
    val name: String? = "",       // Name of the seller
    val email: String? = "",      // Email address of the seller
    val username: String? = "",   // Username chosen by the seller
    var password: String? = "",   // Password of the seller
    val phone: String? = "",      // Phone number of the seller
    val address: String? = "",    // Address of the seller
    val pic: String? = "",        // URL of the seller's profile picture
    var id: Int? = -1             // Unique identifier for the seller (default value: -1)
)
