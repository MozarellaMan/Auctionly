import server.AuctionServer;

public class Main {

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        server.run(1099);
    }
}
