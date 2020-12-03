package server;

import util.Util;

import java.util.Scanner;

import static util.Util.writeKeys;

public class ServerRunner {
    public static final String PRIV_KEY_PATH = "./s_priv_key.txt";
    public static final String PUB_KEY_PATH = "./s_pub_key.txt";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        writeKeys(PUB_KEY_PATH, PRIV_KEY_PATH);
        Scanner scanner = new Scanner(System.in);
        AuctionServer auctionServer = new AuctionServer();
        Thread serverThread = new Thread(auctionServer);
        serverThread.start();
        serverThread.join();

        System.out.println("Type 'commands' to see a list of commands");
        label:
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");
            try {
                inputArgs[0] = inputArgs[0].toLowerCase();
                switch (inputArgs[0]) {
                    case "quit":
                        auctionServer.getAuctionService().getAuctionChannel().closeAll();
                        System.exit(0);
                        break label;
                    case "add":
                        auctionServer.getAuctionService().getAuctionChannel().add();
                        break;
                    case "close-orig":
                        auctionServer.getAuctionService().getAuctionChannel().closeOriginalClusters();
                        break;
                    default:
                        Util.warning("Unsupported command!");
                        break;
                }
            } catch (Exception e) {
                Util.warning("Server exception: " + e.getMessage());
            }
        }


    }
}
