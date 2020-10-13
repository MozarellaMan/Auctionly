package client;

//import server.Auction;

import server.Auction;

import java.rmi.Naming;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
    protected int clientId;

    public Client() {
        this.clientId = ThreadLocalRandom.current().nextInt(1,100);
    }

    public void test(int itemId) {
        try {

            Auction auctionStub = (Auction)
                    Naming.lookup("rmi://localhost/AuctionService");


            System.out.println(auctionStub.getSpecUnsafe(itemId, clientId));

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public int getClientId() {
        return clientId;
    }
}

