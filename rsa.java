import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    // Constructor to generate key pairs
    public RSA(int keySize) {
        // Generate two large prime numbers
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(keySize / 2, random);
        BigInteger q = BigInteger.probablePrime(keySize / 2, random);

        // Compute modulus
        modulus = p.multiply(q);

        // Compute Euler's totient function
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Choose a public exponent (e) relatively prime to phi
        publicKey = BigInteger.probablePrime(keySize / 4, random);
        while (phi.gcd(publicKey).compareTo(BigInteger.ONE) > 0 && publicKey.compareTo(phi) < 0) {
            publicKey.add(BigInteger.ONE);
        }

        // Compute private exponent (d)
        privateKey = publicKey.modInverse(phi);
    }

    // Method to encrypt message using public key
    public BigInteger encrypt(String message) {
        return new BigInteger(message.getBytes()).modPow(publicKey, modulus);
    }

    // Method to decrypt message using private key
    public String decrypt(BigInteger encryptedMessage) {
        return new String(encryptedMessage.modPow(privateKey, modulus).toByteArray());
    }

    // Method to return public key
    public BigInteger getPublicKey() {
        return publicKey;
    }

    // Method to return private key
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) {
        // Example usage
        RSA rsa = new RSA(1024);
        String originalMessage = "Hello, World!";
        BigInteger encryptedMessage = rsa.encrypt(originalMessage);
        String decryptedMessage = rsa.decrypt(encryptedMessage);

        System.out.println("Original Message: " + originalMessage);
        System.out.println("Encrypted Message: " + encryptedMessage);
        System.out.println("Decrypted Message: " + decryptedMessage);
    }
}
