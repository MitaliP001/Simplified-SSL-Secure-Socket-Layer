import java.security.SecureRandom;

public class OneTimeKey {
    // Method to generate a one-time key of given length
    public byte[] generateKey(int length) {
        byte[] key = new byte[length];
        new SecureRandom().nextBytes(key); // Generate random key
        return key;
    }
    
    // Method to encrypt data using a one-time key
    public byte[] encrypt(byte[] data, byte[] key) {
        byte[] encryptedData = new byte[data.length];
        // Implement encryption logic using bitwise exclusive OR (^)
        for (int i = 0; i < data.length; i++) {
            encryptedData[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return encryptedData;
    }
    
    // Method to decrypt data using a one-time key
    public byte[] decrypt(byte[] encryptedData, byte[] key) {
        // Decryption is the same as encryption
        return encrypt(encryptedData, key);
    }
}
  