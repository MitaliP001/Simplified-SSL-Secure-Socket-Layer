package security;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    public RSA(int keySize) {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(keySize / 2, random);
        BigInteger q = BigInteger.probablePrime(keySize / 2, random);
        modulus = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        publicKey = BigInteger.probablePrime(keySize / 4, random);
        while (phi.gcd(publicKey).compareTo(BigInteger.ONE) > 0 && publicKey.compareTo(phi) < 0) {
            publicKey.add(BigInteger.ONE);
        }
        privateKey = publicKey.modInverse(phi);
    }

    public BigInteger encrypt(String message) {
        return new BigInteger(message.getBytes()).modPow(publicKey, modulus);
    }

    public String decrypt(BigInteger encryptedMessage) {
        return new String(encryptedMessage.modPow(privateKey, modulus).toByteArray());
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }
}
