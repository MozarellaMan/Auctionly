package client;

import server.Auction;
import util.SecurityHelper;
import util.Util;

import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.SignedObject;
import java.util.Optional;

public class ClientRequest extends Client implements Serializable {
    private final SecurityHelper<ClientRequest> securityHelper;

    public ClientRequest(){
        super();
        securityHelper = new SecurityHelper<>();
    }

    public void make()  {
        var sealedRequest = securityHelper.encrypt(this, true);
        sealedRequest.ifPresentOrElse(req -> System.out.println("Request creation for client " + clientId + " successful. Used key on system."), () -> Util.warning("Request could not be encrypted!"));
    }

    public void make(String key) {
        var sealedRequest = securityHelper.encrypt(this, key);
        sealedRequest.ifPresentOrElse(req -> System.out.println("Request creation for client " + clientId + " successful. Used key on system."), () -> Util.warning("Request could not be encrypted!"));
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
            Util.warning("Request exception: " + e.getMessage());
        }
        return 0;
    }

    public Optional<SignedObject> sendChallenge(int challenge) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return Optional.of(auctionStub.acceptChallenge(challenge));
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getMessage());
        }
        return Optional.empty();
    }

    public int getChallenge() {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.generateChallenge();
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getMessage());
        }
        return 0;
    }

    public boolean authenticate(int id, PublicKey pKey, SignedObject challenge) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.authenticate(id, pKey, challenge);
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getMessage());
        }
        return false;
    }

    public int sell(float startPrice, float reservePrice, int itemId, int userId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.sell(startPrice, reservePrice, itemId, userId);
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getMessage());
        }
        return 0;
    }

    public boolean bid(int auctionId, float offerPrice, int userId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.bid(auctionId, offerPrice, userId);
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getMessage());
        }
        return false;
    }

    public boolean close(int auctionId, int ownerId) {
        try {
            Auction auctionStub = (Auction) Naming.lookup("rmi://localhost/AuctionService");
            return auctionStub.close(auctionId, ownerId);
        } catch (ConnectException e) {
            Util.warning("Connection could not be made to server!");
        } catch (RemoteException e) {
            Util.warning("Remote call error: " + e.getCause().getMessage());
        } catch (Exception e) {
            Util.warning("Request exception: " + e.getMessage());
        }
        return false;
    }

}
