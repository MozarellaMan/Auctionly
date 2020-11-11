package util;

import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.util.Optional;

import static util.Util.warning;


public class Authenticator<T extends Serializable> {

    public static Optional<KeyPair> generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
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

    public Optional<SignedObject> sign(T unsignedObj, PrivateKey key) {
        try {
            var signature = Signature.getInstance(key.getAlgorithm());
            var signedObj = new SignedObject(unsignedObj, key, signature);
            return Optional.of(signedObj);

        } catch (NoSuchAlgorithmException e) {
            warning("No such algorithm to generate key-pair!");
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
}
