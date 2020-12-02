package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;

import static server.ServerRunner.PRIV_KEY_PATH;


public class AuctionServer implements Runnable {
    private AuctionService auctionService;

    public void run(int port) {
        try {
            AuctionService auctionService = new AuctionService();
            Naming.rebind("rmi://localhost/AuctionService", auctionService);
            System.out.println("Server ready! 🚀 Running on... " + port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void run(int port, String key) {
        try {
            AuctionService service = new AuctionService(key);
            this.auctionService = service;
            Naming.rebind("rmi://localhost:" + port + "/AuctionService", service);
            System.out.println("Server ready! 🚀 Running on... " + port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public AuctionService getAuctionService() {
        return auctionService;
    }

    @Override
    public void run() {
        try {
            var key = new String(Files.readAllBytes(Paths.get(PRIV_KEY_PATH)));
            this.run(1099, key);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
