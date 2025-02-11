package client;

import server.Auction;
import server.item.AuctionItem;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class ClientRequest extends Client implements Serializable {
    private SealedObject request;

    public ClientRequest(){
        super();
    }

    public void make() throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, InvalidKeyException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);

        SecretKey aesKey = keyGen.generateKey();

        Cipher requestCipher = Cipher.getInstance("AES");
        requestCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        System.out.println("Request creation for client " + clientId + " successful. Your key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()) + "\n(Keep this safe!)");

        this.request = new SealedObject(request, requestCipher);
    }


    public void test(int itemId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            System.out.println(auctionStub.getSpec(itemId, request));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public SealedObject testSealed(int itemId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.getSpec(itemId, request);
        } catch (ConnectException e) {
            ClientRunner.warning("Connection could not be made to server!");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public Optional<AuctionItem> requestDecrypt(SecretKey key, SealedObject item) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return Optional.ofNullable(auctionStub.decryptItem(item, key));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
