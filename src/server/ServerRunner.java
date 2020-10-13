package server;

public class ServerRunner {
    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        server.run(1099);
    }
}
