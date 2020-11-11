package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.Util.writeKeys;

public class ServerRunner {
    public static String PRIV_KEY_PATH = "./s_priv_key.txt";
    public static String PUB_KEY_PATH = "./s_pub_key.txt";

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        writeKeys(PUB_KEY_PATH, PRIV_KEY_PATH);
        try {
            var key = new String(Files.readAllBytes(Paths.get(PRIV_KEY_PATH)));
            server.run(1099, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
