package client;

//import server.Auction;

import server.Auction;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public void test(int itemId, int clientId) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);

            Auction auctionStub = (Auction) registry.lookup("Auction");

            System.out.println(auctionStub.getSpec(itemId, clientId));

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
