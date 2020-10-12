package server;

import server.item.AuctionItem;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Auction extends Remote {

    AuctionItem getSpec(int itemId, int clientId) throws RemoteException;

}
