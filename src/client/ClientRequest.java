package client;

import server.Auction;
import util.SecurityHelper;
import util.Util;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ClientRequest extends Client implements Serializable {
    private SealedObject request;
    private final SecurityHelper<ClientRequest> securityHelper;

    public ClientRequest(){
        super();
        securityHelper = new SecurityHelper<>();
    }

    public void make()  {
        var sealedRequest = securityHelper.encrypt(this, true);
        sealedRequest.ifPresentOrElse(req -> {
            System.out.println("Request creation for client " + clientId + " successful. Used key on system.");
            this.request = req;
        }, () -> Util.warning("Request could not be encrypted!"));
    }

    public void make(String key) {
        var sealedRequest = securityHelper.encrypt(this, key);
        sealedRequest.ifPresentOrElse(req -> {
            System.out.println("Request creation for client " + clientId + " successful. Used key on system.");
            this.request = req;
        }, () -> Util.warning("Request could not be encrypted!"));
    }


    public void test(int itemId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            System.out.println(auctionStub.getSpec(itemId, request));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public SealedObject getSealed(int itemId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.getSpec(itemId, request);
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Util.warning("Client exception: " + e.getMessage());
        }
        return null;
    }

}
