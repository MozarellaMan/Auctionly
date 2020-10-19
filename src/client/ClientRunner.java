package client;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class ClientRunner {

    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";
    public static final String KEY_PATH = "./key.txt";

    public static boolean keyExists() {
        var file = Paths.get(KEY_PATH);
        return Files.exists(file);
    }

    public static void warning(String input) {
        System.out.println(RED + input + RESET);
    }

    public static void attemptDecrypt(ClientRequest client, SealedObject object, Scanner scanner) {
        System.out.println("The response is encrypted.");
        SecretKey secretKey = null;
        if (keyExists()) {
            System.out.println("Attempting decryption with on system key...");
            try {
                var key = new String(Files.readAllBytes(Paths.get(KEY_PATH)));
                byte[] decodedKey = Base64.getDecoder().decode(key);
                secretKey = new SecretKeySpec(decodedKey, "AES");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Attempt decryption with key:");
            byte[] encodedKey = Base64.getDecoder().decode(scanner.nextLine());
            if (encodedKey.length != 16) {
                warning("Key is not the required length!");
                return;
            }
            secretKey = new SecretKeySpec(encodedKey, "AES");

        }
        var item = client.requestDecrypt(secretKey, object);
        item.ifPresentOrElse(System.out::println, () -> warning("Item could not be decrypted!"));
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
                var sealedObj = request.testSealed(id);
                if (sealedObj != null) attemptDecrypt(request, sealedObj, scanner);
            } else if (inputArgs[0].equals("quit")) {
                System.out.println("Bye!");
                break;
            } else {
                warning("Unsupported command!");
            }
        }
        scanner.close();
    }
}
