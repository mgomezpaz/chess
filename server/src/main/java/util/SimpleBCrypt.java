package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * A simplified BCrypt-like password hashing implementation
 * Note: This is NOT as secure as the real BCrypt, but provides similar functionality
 * without external dependencies.
 */
public class SimpleBCrypt {
    private static final int DEFAULT_COST = 10;
    private static final String ALGORITHM = "SHA-256";
    private static final String SALT_PREFIX = "$2a$";
    private static final String SALT_SEPARATOR = "$";
    
    /**
     * Generates a salt for password hashing
     * @param cost the computational cost (higher is more secure but slower)
     * @return a salt string
     */
    public static String gensalt(int cost) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        // Encode the salt and cost as a string
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        return SALT_PREFIX + cost + SALT_SEPARATOR + encodedSalt;
    }
    
    /**
     * Generates a salt with default cost
     * @return a salt string
     */
    public static String gensalt() {
        return gensalt(DEFAULT_COST);
    }
    
    /**
     * Hashes a password with the given salt
     * @param password the password to hash
     * @param salt the salt to use
     * @return the hashed password
     */
    public static String hashpw(String password, String salt) {
        try {
            // Extract the cost and salt value
            String[] parts = salt.split("\\$");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Invalid salt format");
            }
            
            int cost = Integer.parseInt(parts[2]);
            String saltValue = parts[3];
            byte[] saltBytes = Base64.getDecoder().decode(saltValue);
            
            // Combine password and salt
            byte[] passwordBytes = password.getBytes();
            byte[] combined = new byte[passwordBytes.length + saltBytes.length];
            System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
            System.arraycopy(saltBytes, 0, combined, passwordBytes.length, saltBytes.length);
            
            // Hash multiple times based on cost
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = combined;
            
            for (int i = 0; i < (1 << cost); i++) {
                hash = digest.digest(hash);
            }
            
            // Encode the final hash
            String hashValue = Base64.getEncoder().encodeToString(hash);
            
            // Return in BCrypt-like format
            return salt + SALT_SEPARATOR + hashValue;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Checks if a password matches a hashed value
     * @param password the password to check
     * @param hashedPassword the hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean checkpw(String password, String hashedPassword) {
        try {
            // Extract the salt from the hashed password
            String[] parts = hashedPassword.split("\\$");
            if (parts.length < 5) {
                return false; // Invalid format
            }
            
            // Reconstruct the salt
            String salt = SALT_PREFIX + parts[2] + SALT_SEPARATOR + parts[3];
            
            // Hash the password with the extracted salt
            String newHash = hashpw(password, salt);
            
            // Compare the hashes
            return newHash.equals(hashedPassword);
            
        } catch (Exception e) {
            return false; // Any error means no match
        }
    }
} 