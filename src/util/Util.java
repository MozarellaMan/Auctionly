package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyException;
import java.util.Base64;

public class Util {
    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";

    public static void warning(String input) {
        System.out.println(RED + input + RESET);
    }

    public static void writeKeys(String pubkeyPath, String privKeyPath) {
        var pubKeyFile = new File(pubkeyPath);
        var privKeyFile = new File(privKeyPath);

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
}
