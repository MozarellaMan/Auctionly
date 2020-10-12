package server;

import server.item.AuctionItem;
import server.item.ItemRepository;

import java.rmi.RemoteException;

public class AuctionService implements Auction {

    @Override
    public AuctionItem getSpec(int itemId, int clientId) throws RemoteException {
        System.out.println("Request for client " + clientId + ":");
        return ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Client " + clientId + " failed:\n\tAuction item with id " + itemId + " does not exist!")
        );
    }
}
