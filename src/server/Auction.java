package server;

import server.item.AuctionItem;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface Auction extends Remote {

    AuctionItem getSpecUnsafe(int itemId, int clientId) throws RemoteException;

    SealedObject getSpec(int itemId, SealedObject clientRequest) throws IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException;

}
