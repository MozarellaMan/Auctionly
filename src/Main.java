
import client.ClientRequest;
import server.AuctionServer;
public class Main {

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        server.run(1099);

        try {
            ClientRequest request = new ClientRequest();
            request.make();

        } catch (Exception e) {
            System.err.println("Client request creation error: ");
            e.printStackTrace();
        }

    }
}
