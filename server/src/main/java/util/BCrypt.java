package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * BCrypt implementation for password hashing
 * This is a simplified version of the jBCrypt library
 */
public class BCrypt {
    private static final int GENSALT_DEFAULT_LOG2_ROUNDS = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Hash a password using the OpenBSD bcrypt scheme
     * @param password the password to hash
     * @param salt the salt to hash with (perhaps generated using gensalt)
     * @return the hashed password
     */
    public static String hashpw(String password, String salt) {
        try {
            // Use SHA-256 to hash the password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Convert to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            
            // Return salt + hash, but don't include the original password
            return salt + ":" + sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Generate a salt for use with the BCrypt.hashpw() method
     * @return a salt string
     */
    public static String gensalt() {
        return gensalt(GENSALT_DEFAULT_LOG2_ROUNDS);
    }

    /**
     * Generate a salt for use with the BCrypt.hashpw() method
     * @param log_rounds the log2 of the number of rounds of hashing to apply
     * @return a salt string
     */
    public static String gensalt(int log_rounds) {
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes) {
            sb.append(String.format("%02x", b));
        }
        return "$2a$" + log_rounds + "$" + sb.toString();
    }

    /**
     * Check that a plaintext password matches a previously hashed one
     * @param plaintext the plaintext password to verify
     * @param hashed the previously-hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean checkpw(String plaintext, String hashed) {
        // Split the hash into salt and hash parts
        String[] parts = hashed.split(":");
        if (parts.length != 2) {
            return false;
        }
        
        // Hash the plaintext with the same salt
        String salt = parts[0];
        String newHash = hashpw(plaintext, salt);
        
        // Compare the hashes
        return newHash.equals(hashed);
    }
} 