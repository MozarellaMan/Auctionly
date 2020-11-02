package server;

import server.item.Item;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Auction extends Remote {

    Item getSpecUnsafe(int itemId, int clientId) throws RemoteException;

    SealedObject getSpec(int itemId, SealedObject clientRequest) throws RemoteException;

    List<Item> getActiveAuctions() throws RemoteException;

    boolean bid(int auctionId, String name, String email) throws RemoteException;

    int sell(float startPrice, float reservePrice, Item item) throws RemoteException;

    boolean close(int auctionId, int buyerId) throws RemoteException;

    boolean close(int auctionId) throws RemoteException;
}
