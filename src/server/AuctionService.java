package server;

import server.item.AuctionItem;
import server.item.ItemRepository;
import util.SecurityHelper;

import javax.crypto.SealedObject;
import java.rmi.RemoteException;

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
    public SealedObject getSpec(int itemId, SealedObject clientRequest) throws RemoteException {
        System.out.println("Request for encrypted client " + clientRequest);
        AuctionItem item = ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Encrypted client failed:\n\tAuction item with id " + itemId + " does not exist!")
        );

        var itemSecurityHelper = new SecurityHelper<AuctionItem>();

        if (key == null) {
            return itemSecurityHelper.encrypt(item, true).orElseThrow(
                    () -> new RemoteException("Error: Auction item response could not be encrypted."));
        } else {
            return itemSecurityHelper.encrypt(item, key).orElseThrow(
                    () -> new RemoteException("Error: Auction item response could not be encrypted.")
            );
        }

    }

}
