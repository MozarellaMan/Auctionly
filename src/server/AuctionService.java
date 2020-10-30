package server;

import server.item.AuctionItem;
import server.item.ItemRepository;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuctionService extends java.rmi.server.UnicastRemoteObject implements Auction {

    private String key;

    protected AuctionService() throws RemoteException {
        super();
    }

    protected AuctionService(String key) throws RemoteException {
        super();
        this.key = key;
    }

    @Override
    public AuctionItem getSpecUnsafe(int itemId, int clientId) throws RemoteException {
        System.out.println("Request for client " + clientId + ":");
        return ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Client " + clientId + " failed:\n\tAuction item with id " + itemId + " does not exist!")
        );
    }

    @Override
    public SealedObject getSpec(int itemId, SealedObject clientRequest) throws IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        System.out.println("Request for encrypted client " + clientRequest);
        AuctionItem item = ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Encrypted client failed:\n\tAuction item with id " + itemId + " does not exist!")
        );

        SecretKey aesKey;

        if (key == null) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            aesKey = keyGen.generateKey();
            System.out.println("Request creation for client " + clientRequest + " successful. Your key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()) + "\n(Keep this safe!)");
        } else {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            aesKey = new SecretKeySpec(decodedKey, "AES");
        }


        Cipher auctionItemCipher = Cipher.getInstance("AES");
        auctionItemCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        return new SealedObject(item, auctionItemCipher);
    }

}
