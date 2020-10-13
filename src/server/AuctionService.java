package server;

import server.item.AuctionItem;
import server.item.ItemRepository;


import javax.crypto.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AuctionService extends java.rmi.server.UnicastRemoteObject implements Auction {

    protected AuctionService() throws RemoteException {
        super();
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

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);

        SecretKey aesKey = keyGen.generateKey();

        Cipher auctionItemCipher = Cipher.getInstance("AES");
        auctionItemCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        return new SealedObject(item, auctionItemCipher);
    }
}
