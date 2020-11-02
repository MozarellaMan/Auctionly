package server;

import server.auctions.AuctionRepostiory;
import server.item.Item;
import server.item.ItemRepository;
import util.SecurityHelper;

import javax.crypto.SealedObject;
import java.rmi.RemoteException;
import java.util.List;

public class AuctionService extends java.rmi.server.UnicastRemoteObject implements Auction {

    private String key;
    private AuctionRepostiory auctions;

    protected AuctionService() throws RemoteException {
        super();
    }

    protected AuctionService(String key) throws RemoteException {
        super();
        this.key = key;
        this.auctions = new AuctionRepostiory();
    }

    @Override
    public Item getSpecUnsafe(int itemId, int clientId) throws RemoteException {
        System.out.println("Request for client " + clientId + ":");
        return ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Client " + clientId + " failed:\n\tAuction item with id " + itemId + " does not exist!")
        );
    }

    @Override
    public SealedObject getSpec(int itemId, SealedObject clientRequest) throws RemoteException {
        System.out.println("Request for encrypted client " + clientRequest);
        Item item = ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Encrypted client failed:\n\tAuction item with id " + itemId + " does not exist!")
        );

        var itemSecurityHelper = new SecurityHelper<Item>();

        if (key == null) {
            return itemSecurityHelper.encrypt(item, true).orElseThrow(
                    () -> new RemoteException("Error: Auction item response could not be encrypted."));
        } else {
            return itemSecurityHelper.encrypt(item, key).orElseThrow(
                    () -> new RemoteException("Error: Auction item response could not be encrypted.")
            );
        }

    }

    @Override
    public List<Item> getActiveAuctions() throws RemoteException {
        return null;
    }

    @Override
    public boolean bid(int auctionId, String name, String email) throws RemoteException {
        return false;
    }

    @Override
    public int sell(float startPrice, float reservePrice, Item item) throws RemoteException {
        return 0;
    }

    @Override
    public boolean close(int auctionId, int buyerId) throws RemoteException {
        return false;
    }

    @Override
    public boolean close(int auctionId) throws RemoteException {
        return false;
    }

}
