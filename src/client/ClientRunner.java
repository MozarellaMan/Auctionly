package client;

import util.Util;

import java.io.File;
import java.util.Scanner;

public class ClientRunner {

    public static String PRIV_KEY_PATH = "./c_priv_key";
    public static String PUB_KEY_PATH = "./c_pub_key";
    // public static final SecurityHelper<Item> ITEM_SECURITY_HELPER = new SecurityHelper<>();


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientRequest request = new ClientRequest();
        int id = 0;

        System.out.println("Enter a request: ");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");

            if (inputArgs[0].equals("register")) {
                if (inputArgs.length > 4) continue;
                String name = inputArgs[1];
                String email = inputArgs[2];
                String role = inputArgs[3].toUpperCase();

                id = request.register(name, email, role);
                System.out.println("Your user ID: " + id);
                Util.warning("This account is NOT authenticated!");
                Util.writeKeys(PUB_KEY_PATH + id + ".txt", PRIV_KEY_PATH + id + ".txt");

            } else if (inputArgs.length == 2 && inputArgs[0].equals("get-spec")) {
                int itemId = Integer.parseInt(inputArgs[1]);
                request.getSpec(itemId, id);
            } else if (inputArgs[0].equals("quit")) {
                System.out.println("Bye!");
                File priv = new File(PRIV_KEY_PATH + id + ".txt");
                File pub = new File(PUB_KEY_PATH + id + ".txt");
                if (priv.delete() && pub.delete())
                    System.out.println("Cleaned up keys.");
                else
                    System.out.println("Failed to clean up keys. They are still there or none were made.");
                break;
            } else {
                Util.warning("Unsupported command!");
            }
        }
        scanner.close();
    }
}
