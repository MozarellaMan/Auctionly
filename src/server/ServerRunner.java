package server;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ServerRunner {
    public static String KEY_PATH = "./key.txt";

    public static void writeKey() {
        var file = new File(KEY_PATH);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);

            SecretKey aesKey = keyGen.generateKey();


            writer.write(Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        writeKey();
        try {
            var key = new String(Files.readAllBytes(Paths.get(KEY_PATH)));
            server.run(1099, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
