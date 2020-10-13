package server;

import java.rmi.Naming;


public class AuctionServer {
    public void run(int port) {
        try {
            AuctionService auctionService = new AuctionService();
            Naming.rebind("rmi://localhost/AuctionService", auctionService);
            System.out.println("Server ready! 🚀 Running on... " +  port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
