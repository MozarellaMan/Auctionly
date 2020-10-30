package client;

import server.item.AuctionItem;
import util.SecurityHelper;
import util.Util;

import javax.crypto.SealedObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientRunner {

    public static final String KEY_PATH = "./key.txt";
    public static final SecurityHelper<AuctionItem> AUCTION_ITEM_SECURITY = new SecurityHelper<>();

    public static boolean keyExists() {
        var file = Paths.get(KEY_PATH);
        return Files.exists(file);
    }

    public static void attemptDecrypt(SealedObject object, Scanner scanner) {
        System.out.println("The response is encrypted.");
        boolean keyExists = keyExists();
        String onSystemKey = null;
        if (keyExists) {
            System.out.println("Attempting decryption with on system key...");
            try {
                onSystemKey = new String(Files.readAllBytes(Paths.get(KEY_PATH)));
            } catch (IOException e) {
                Util.warning("Error reading key file: " +  e.getMessage());
            }
        } else {
            System.out.println("Attempt decryption with key:");
        }

        var secretKey = keyExists ? SecurityHelper.keyFromString(onSystemKey) : SecurityHelper.keyFromString(scanner.nextLine());
        var item = AUCTION_ITEM_SECURITY.decrypt(object,secretKey);
        item.ifPresentOrElse(System.out::println, () -> Util.warning("Item could not be decrypted!"));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientRequest request = new ClientRequest();

        try {
            if (keyExists()) {
                var key = new String(Files.readAllBytes(Paths.get(KEY_PATH)));
                request.make(key);
            } else {
                request.make();
            }
        } catch (Exception e) {
            System.err.println("Client request creation error: ");
            e.printStackTrace();
        }

        System.out.println("Enter a request: ");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");

            if (inputArgs.length == 2 && inputArgs[0].equals("get-spec")) {
                int id = Integer.parseInt(inputArgs[1]);
                var sealedObj = request.getSealed(id);
                if (sealedObj != null) attemptDecrypt(sealedObj, scanner);
            } else if (inputArgs[0].equals("quit")) {
                System.out.println("Bye!");
                break;
            } else {
                Util.warning("Unsupported command!");
            }
        }
        scanner.close();
    }
}
