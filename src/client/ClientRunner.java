package client;

import server.item.Item;
import util.SecurityHelper;
import util.Util;

import java.util.Scanner;

public class ClientRunner {

    public static String PRIV_KEY_PATH = "./c_priv_key";
    public static String PUB_KEY_PATH = "./c_pub_key";
    public static final SecurityHelper<Item> ITEM_SECURITY_HELPER = new SecurityHelper<>();


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientRequest request = new ClientRequest();

        System.out.println("Enter a request: ");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");

            if (inputArgs.length == 4 && inputArgs[0].equals("register")) {
                String name = inputArgs[1];
                String email = inputArgs[2];
                String role = inputArgs[3].toUpperCase();

                int id = request.register(name, email, role);
                System.out.println(id);

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
