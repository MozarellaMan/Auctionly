package server;

import util.Authenticator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyException;
import java.util.Base64;

public class ServerRunner {
    public static String PRIV_KEY_PATH = "./s_priv_key.txt";
    public static String PUB_KEY_PATH = "./s_pub_key.txt";

    public static void writeKey() {
        var pubKeyFile = new File(PUB_KEY_PATH);
        var privKeyFile = new File(PRIV_KEY_PATH);

        FileWriter pubKeyWriter = null;
        FileWriter privKeyWriter = null;
        try {
            var keyPair = Authenticator.generateKey().orElseThrow(KeyException::new);

            pubKeyWriter = new FileWriter(pubKeyFile);
            pubKeyWriter.write(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

            privKeyWriter = new FileWriter(privKeyFile);
            privKeyWriter.write(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

        } catch (IOException | KeyException e) {
            e.printStackTrace();
        } finally {
            try {
                assert pubKeyWriter != null;
                assert privKeyWriter != null;
                pubKeyWriter.close();
                privKeyWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        writeKey();
        try {
            var key = new String(Files.readAllBytes(Paths.get(PRIV_KEY_PATH)));
            server.run(1099, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
