package server;

import server.item.AuctionItem;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Auction extends Remote {

    AuctionItem getSpecUnsafe(int itemId, int clientId) throws RemoteException;

    SealedObject getSpec(int itemId, SealedObject clientRequest) throws RemoteException;

}
