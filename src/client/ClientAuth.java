package client;

import util.Authenticator;
import util.Util;

import java.security.Signature;
import java.util.concurrent.ThreadLocalRandom;

public class ClientAuth {

    public static String SPUB_KEY_PATH = "./s_pub_key.txt";

    public static void verifyServer(int id, ClientRequest client) {
        System.out.println("Sending server authentication challenge...");

        int challenge = ThreadLocalRandom.current().nextInt(1000, 10000000);
        try {
            var signedChallenge = client.sendChallenge(challenge);
            if (signedChallenge.isEmpty()) {
                Util.warning("Server did not respond to challenge. Authentication Failed!");
                return;
            }

            var serverPublicKey = Authenticator.publicKeyFromFileRSA(SPUB_KEY_PATH).orElseThrow();
            var signature = Signature.getInstance(serverPublicKey.getAlgorithm());
            var isVerified = Authenticator.verify(serverPublicKey, signedChallenge.get(), signature);

            if (isVerified) {
                System.out.println("Challenge to server has been verified!");
                verifyClient(id, client);
            } else {
                Util.warning("Server could not be verified. Authentication failed!");
            }
        } catch (Exception e) {
            Util.warning("Authentication exception: " + e.getClass().getName());
        }
    }

    private static void verifyClient(int id, ClientRequest client) {
        int serverChallenge = client.getChallenge();
        if (serverChallenge == 0) {
            Util.warning("Server did not send a challenge! Authentication failed.");
            return;
        }
        String clientPubKeyPath = ClientRunner.PUB_KEY_PATH + id + ".txt";
        String clientPrivKeyPath = ClientRunner.PRIV_KEY_PATH + id + ".txt";
        var authenticator = new Authenticator<Integer>();
        try {
            System.out.println("Received server challenge. Attempting to sign and send to server to verify...");
            var publicKey = Authenticator.publicKeyFromFileRSA(clientPubKeyPath).orElseThrow();
            var privateKey = Authenticator.privateKeyFromFileDSA(clientPrivKeyPath).orElseThrow();
            var signedChallenge = authenticator.sign(serverChallenge, privateKey).orElseThrow();

            var authenticated = client.authenticate(id, publicKey, signedChallenge);
            if (authenticated) {
                System.out.println("You are now authenticated with the server!");
            } else {
                Util.warning("The server could not verify you. Authentication failed.");
            }
        } catch (Exception e) {
            Util.warning("Authentication exception: " + e.getClass().getName());
        }

    }

}
