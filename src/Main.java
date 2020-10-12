import client.Client;
import server.AuctionServer;

public class Main {

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        server.run(1099);

        Client client = new Client();

        client.test(1, 5);
        client.test(2, 5);
        client.test(3, 5);
        client.test(4, 5);
    }
}
