package util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import static util.Util.warning;

public class SecurityHelper<T extends Serializable> implements Serializable {

    public static SecretKey keyFromString(String input) {
        byte[] decodedKey = Base64.getDecoder().decode(input);
        return new SecretKeySpec(decodedKey, "AES");
    }


    public Optional<SealedObject> encrypt(T item, SecretKey key) {
        try {
            Cipher requestCipher = Cipher.getInstance("AES");
            requestCipher.init(Cipher.ENCRYPT_MODE, key);
            return Optional.of(new SealedObject(item, requestCipher));
        } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            warning("Encryption failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    public Optional<SealedObject> encrypt(T item, String key) {
        try {
            Cipher requestCipher = Cipher.getInstance("AES");
            requestCipher.init(Cipher.ENCRYPT_MODE, keyFromString(key));
            return Optional.of(new SealedObject(item, requestCipher));
        } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            warning("Encryption failed. Reason: " + e.getClass().getName());
            return Optional.empty();
        }
    }

    public Optional<SealedObject> encrypt(T item, boolean showKey) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();
            if (showKey) {
                System.out.println("Encryption of " + item.getClass().getName() + " successful. Your key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()) + "\n(Keep this safe!)");
            }
            return encrypt(item, aesKey);
        } catch (NoSuchAlgorithmException e) {
            warning("Encryption failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<T> decrypt(SealedObject item, SecretKey key)  {
        try {
            return Optional.ofNullable((T) item.getObject(key));
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
            warning("Decryption failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    public Optional<T> decrypt(SealedObject item, String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return decrypt(item, decodedKey);
    }

    public Optional<T> decrypt(SealedObject item, byte[] decodedKey)  {
        if (decodedKey.length != 16) {
            warning("Key is not the required length!");
            return Optional.empty();
        }
        var secretKey = new SecretKeySpec(decodedKey, "AES");
        return decrypt(item, secretKey);
    }

}
