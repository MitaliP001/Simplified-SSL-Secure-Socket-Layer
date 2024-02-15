package security;

import java.security.SecureRandom;

public class OneTimeKey {
    public byte[] generateKey(int length) {
        byte[] key = new byte[length];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public byte[] encrypt(byte[] data, byte[] key) {
        byte[] encryptedData = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encryptedData[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return encryptedData;
    }

    public byte[] decrypt(byte[] encryptedData, byte[] key) {
        return encrypt(encryptedData, key);
    }
}
