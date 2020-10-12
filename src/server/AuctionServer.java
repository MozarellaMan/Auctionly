package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AuctionServer extends AuctionService {
    public void run(int port) {
        try {
            AuctionService auctionService = new AuctionService();

            Auction auctionStub = (Auction) UnicastRemoteObject.exportObject(auctionService, 0);

            Registry registry = LocateRegistry.createRegistry(port);

            registry.bind("Auction", auctionStub);
            System.out.println("Server ready! 🚀");


        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
