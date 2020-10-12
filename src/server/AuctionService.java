package server;

import java.rmi.RemoteException;

public class AuctionService implements Auction {

    @Override
    public AuctionItem getSpec(int itemId, int clientId) throws RemoteException {
        System.out.println(itemId + " " + clientId);
        return null;
    }
}
