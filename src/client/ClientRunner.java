package client;

import server.Auction;
import server.auctions.AuctionItem;
import util.Util;

import java.io.File;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class ClientRunner {

    public static String PRIV_KEY_PATH = "./c_priv_key";
    public static String PUB_KEY_PATH = "./c_pub_key";
    public static int id = 0;
    // public static final SecurityHelper<Item> ITEM_SECURITY_HELPER = new SecurityHelper<>();


    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ClientRunner::cleanup, "Shutdown-thread"));
        Scanner scanner = new Scanner(System.in);
        ClientRequest request = new ClientRequest();

        System.out.println("Enter a request: ");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");
            try {
                Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
                if (inputArgs[0].equals("register")) {
                    if (inputArgs.length > 4) continue;
                    String name = inputArgs[1];
                    String email = inputArgs[2];
                    String role = inputArgs[3].toUpperCase();
                    if (id == 0) {
                        id = request.register(name, email, role);
                        System.out.println("Your user ID: " + id);
                        Util.warning("This account is NOT authenticated! Please authenticate.");
                        Util.writeKeys(PUB_KEY_PATH + id + ".txt", PRIV_KEY_PATH + id + ".txt");
                        ClientAuth.verifyServer(id, request);
                    } else {
                        Util.warning("User already registered!");
                    }
                } else if (inputArgs.length == 2 && inputArgs[0].equals("get-spec")) {
                    int itemId = Integer.parseInt(inputArgs[1]);
                    request.getSpec(itemId, id);
                } else if (inputArgs[0].equals("sell")) {
                    float startPrice = Float.parseFloat(inputArgs[2]);
                    float reservePrice = Float.parseFloat(inputArgs[3]);
                    int itemId = Integer.parseInt(inputArgs[1]);
                    int auctionId = auctionStub.sell(startPrice, reservePrice, itemId, id);
                    if (auctionId > 0) {
                        System.out.println("Selling successful. Your auction ID: " + auctionId);
                        continue;
                    }
                    Util.warning("Item was not sold.");
                } else if (inputArgs[0].equals("bid")) {
                    int auctionId = Integer.parseInt(inputArgs[1]);
                    float offer = Float.parseFloat(inputArgs[2]);
                    boolean success = auctionStub.bid(auctionId, offer, id);
                    if (success) {
                        System.out.println("Bid successful! Active auctions:");
                        printAuctions(auctionStub.getActiveAuctions(id));
                        continue;
                    }
                    Util.warning("Bid unsuccessful.");
                } else if (inputArgs[0].equals("close")) {
                    int auctionId = Integer.parseInt(inputArgs[1]);
                    boolean success = auctionStub.close(auctionId, id);
                    if (success) {
                        System.out.println("Auction #" + auctionId + " closed! Current closed auctions:");
                        printAuctions(auctionStub.getClosedAuctions(id));
                        continue;
                    }
                    Util.warning("Auction could not be closed.");
                } else if (inputArgs[0].equals("list-active")) {
                    printAuctions(auctionStub.getActiveAuctions(id));
                } else if (inputArgs[0].equals("list-closed")) {
                    printAuctions(auctionStub.getClosedAuctions(id));
                } else if (inputArgs[0].equals("quit")) {
                    System.exit(0);
                    break;
                } else {
                    Util.warning("Unsupported command!");
                }
            } catch (ConnectException e) {
                Util.warning("Connection could not be made to server!");
            } catch (RemoteException e) {
                Util.warning("Remote call error: " + e.getCause().getMessage());
            } catch (Exception e) {
                Util.warning("Client exception: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void cleanup() {
        if (id != 0) {
            File priv = new File(PRIV_KEY_PATH + id + ".txt");
            File pub = new File(PUB_KEY_PATH + id + ".txt");
            if (priv.delete() && pub.delete())
                System.out.println("Cleaned up keys.");
            else
                System.out.println("Failed to clean up keys. They are still there or none were made.");
        }
        System.out.println("Bye!");
    }

    private static void printAuctions(List<AuctionItem> items) {
        if (items.isEmpty()) {
            System.out.println("No Auctions!");
            return;
        }
        items.forEach(System.out::println);
    }

}
