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


    public void getSpec(int itemId, int userId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            System.out.println(auctionStub.getSpecAuth(itemId, userId));
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getCause().getMessage());
        }
    }

    public int register(String name, String email, String role) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.registerUser(name, email, role);
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getCause().getMessage());
        }
        return 0;
    }

}
