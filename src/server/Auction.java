package server;

import server.auctions.AuctionItem;
import server.item.Item;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.SignedObject;
import java.util.List;

public interface Auction extends Remote {

    Item getSpecAuth(int itemId, int clientId) throws RemoteException;

    SealedObject getSpec(int itemId, SealedObject clientRequest) throws RemoteException;

    int registerUser(String name, String email, String role) throws RemoteException;

    List<AuctionItem> getActiveAuctions(int userId) throws RemoteException;

    List<AuctionItem> getClosedAuctions(int userId) throws RemoteException;

    boolean bid(int auctionId, float offerPrice, int userId) throws RemoteException;

    int sell(float startPrice, float reservePrice, Item item, int userId) throws RemoteException;

    boolean close(int auctionId, int ownerId) throws RemoteException;

    boolean authenticate(int userId, PublicKey key, SignedObject challenge) throws RemoteException;

    SignedObject acceptChallenge(int challenge) throws RemoteException;

    int generateChallenge() throws RemoteException;
}
