import java.security.MessageDigest

object HashUtil {
    // Function to hash a password using SHA-256 algorithm
    fun hashPassword(password: String): String {
        // Convert the password string to bytes
        val bytes = password.toByteArray()
        // Get an instance of MessageDigest with SHA-256 algorithm
        val md = MessageDigest.getInstance("SHA-256")
        // Generate the hash value for the password bytes
        val digest = md.digest(bytes)
        // Convert the hash bytes to a hexadecimal string representation
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
