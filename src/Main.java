import client.Client;
import client.ClientRequest;
import server.AuctionServer;
public class Main {

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        server.run(1099);

        try {
            ClientRequest request = new ClientRequest();
            request.make();
            request.test(3);

        } catch (Exception e) {
            System.err.println("Client request creation error: ");
            e.printStackTrace();
        }

//        Client client = new Client();
//
//        client.test(1);
//        client.test(2);
//        client.test(3);
//        client.test(4);
    }
}
