package util;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

import static util.Util.warning;


public class Authenticator<T extends Serializable> {

    private static final String ALGO = "DSA";

    public static Optional<KeyPair> generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            return Optional.of(pair);
        } catch (NoSuchAlgorithmException e) {
            warning("No such algorithm to generate key-pair!");
            return Optional.empty();
        } catch (NoSuchProviderException e) {
            warning("No such provider to generate key-pair!");
            return Optional.empty();
        }
    }

    public static boolean verify(PublicKey publicKey, SignedObject signedObject, Signature signature) {
        try {
            return signedObject.verify(publicKey, signature);
        } catch (InvalidKeyException e) {
            warning("Invalid key!");
            return false;
        } catch (SignatureException e) {
            warning("Invalid signature!");
            return false;
        }
    }

    public static Optional<PrivateKey> privateKeyFromStringRSA(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

        try {
            KeyFactory fact = KeyFactory.getInstance(ALGO);
            PrivateKey priv = fact.generatePrivate(keySpec);
            return Optional.of(priv);
        } catch (Exception e) {
            warning("Private key retrieval failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<T> unsign(SignedObject signedObject) {
        try {
            return Optional.of((T) signedObject.getObject());
        } catch (IOException e) {
            warning("IO issue is preventing object from being unsigned!");
            return Optional.empty();
        } catch (ClassNotFoundException e) {
            warning("Signed object could not be unsigned due to unrecognised class!");
            return Optional.empty();
        }
    }

    public static Optional<PublicKey> publicKeyFromStringRSA(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

        try {
            KeyFactory fact = KeyFactory.getInstance(ALGO);
            PublicKey priv = fact.generatePublic(keySpec);
            return Optional.of(priv);
        } catch (Exception e) {
            warning("Public key retrieval failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    public static Optional<PublicKey> publicKeyFromFileRSA(String path) {
        try {
            var keyString = new String(Files.readAllBytes(Paths.get(path)));
            var publicKey = publicKeyFromStringRSA(keyString).orElseThrow();
            return Optional.of(publicKey);
        } catch (Exception e) {
            warning("Public key retrieval failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    public static Optional<PrivateKey> privateKeyFromFileDSA(String path) {
        try {
            var keyString = new String(Files.readAllBytes(Paths.get(path)));
            var privateKey = privateKeyFromStringRSA(keyString).orElseThrow();
            return Optional.of(privateKey);
        } catch (Exception e) {
            warning("Private key retrieval failed. Reason:" + e.getClass().getName());
            return Optional.empty();
        }
    }

    public Optional<SignedObject> sign(T unsignedObj, PrivateKey key) {
        try {
            var signature = Signature.getInstance(key.getAlgorithm());
            var signedObj = new SignedObject(unsignedObj, key, signature);
            return Optional.of(signedObj);

        } catch (NoSuchAlgorithmException e) {
            warning("No such algorithm to generate key-pair!");
            e.printStackTrace();
            return Optional.empty();
        } catch (IOException e) {
            warning("IO issue is preventing object from being signed!");
            return Optional.empty();
        } catch (SignatureException e) {
            warning("A problem occurred with signing the object");
            return Optional.empty();
        } catch (InvalidKeyException e) {
            warning("Invalid key!");
            return Optional.empty();
        }
    }
}
