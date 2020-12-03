package server;

import server.auctions.AuctionItem;
import server.auctions.AuctionRepoCluster;
import server.item.Item;
import server.item.ItemRepository;
import server.user.Role;
import server.user.UserRepository;
import server.user.UserSecurity;
import util.Authenticator;
import util.SecurityHelper;

import javax.crypto.SealedObject;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignedObject;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AuctionService extends java.rmi.server.UnicastRemoteObject implements Auction {

    private String key;
    private AuctionRepoCluster auctionChannel;
    private UserRepository users;
    private UserSecurity userSecurity;

    protected AuctionService() throws RemoteException {
        super();
    }

    protected AuctionService(String key) throws RemoteException {
        super();
        this.key = key;
        this.users = new UserRepository();
        this.userSecurity = new UserSecurity();
        this.auctionChannel = new AuctionRepoCluster();
        auctionChannel.startAll();
    }

    public AuctionRepoCluster getAuctionChannel() {
        return auctionChannel;
    }

    @Override
    public int registerUser(String name, String email, String role) throws RemoteException {
        var newUser = users.register(name, email, Role.valueOf(role));
        if (newUser > 0) {
            userSecurity.addUser(users.get(newUser).orElseThrow());
            return newUser;
        } else
            throw new RemoteException("User could not be registered!");
    }

    @Override
    public boolean authenticate(int userId, PublicKey key, SignedObject challenge) throws RemoteException {
        if (!users.exists(userId))
            throw new RemoteException("User does not exist!");
        try {
            var signature = Signature.getInstance(key.getAlgorithm());
            var verified = Authenticator.verify(key, challenge, signature);
            if (!verified) throw new RemoteException("User could not be verified!");
            return userSecurity.authenticate(userId, users);
        } catch (NoSuchAlgorithmException e) {
            throw new RemoteException("Signature could not be retrieved from public key!");
        }

    }

    @Override
    public SignedObject acceptChallenge(int challenge) throws RemoteException {
        var privateKey = Authenticator.privateKeyFromStringRSA(key).orElseThrow(() ->
                new RemoteException("Server private key could not be retrieved!")
        );

        var authenticator = new Authenticator<Integer>();

        return authenticator.sign(challenge, privateKey).orElseThrow(() ->
                new RemoteException("Server could not sign challenge with private key!")
        );
    }

    @Override
    public int generateChallenge() throws RemoteException {
        return ThreadLocalRandom.current().nextInt(1000000, 10000000);
    }


    @Override
    public List<AuctionItem> getActiveAuctions(int userId) throws RemoteException {
        authCheck(userId);
        return auctionChannel.get().orElseThrow().list();
    }

    @Override
    public List<AuctionItem> getClosedAuctions(int userId) throws RemoteException {
        authCheck(userId);
        return auctionChannel.get().orElseThrow().listClosed();
    }

    @Override
    public synchronized boolean bid(int auctionId, float offerPrice, int userId) throws RemoteException {
        var auction = auctionChannel.get().orElseThrow().get(auctionId).orElseThrow(() ->
                new RemoteException("Auction item does not exist!")
        );
        if (auction.getLatestPrice() >= offerPrice)
            throw new RemoteException("Bid offer is not high enough!");
        var user = users.get(userId).orElseThrow(() ->
                new RemoteException("User does not exist!")
        );
        authCheck(userId);
        if (user.equals(auction.getOwner()))
            throw new RemoteException("User is the owner of the auction item. The owner cannot bid!");

        if (user.getUserRole() == Role.SELLER)
            throw new RemoteException("User is a seller, and cannot bid on auction items!");

        try {
            auctionChannel.sendBid(auction.getId(), user, offerPrice);
            return true;
        } catch (Exception e) {
            throw new RemoteException("Bid could not be sent to cluster: " + e.getMessage());
        }
    }

    @Override
    public int sell(float startPrice, float reservePrice, int itemId, int userId) throws RemoteException {
        var user = users.get(userId).orElseThrow(() ->
                new RemoteException("User does not exist!")
        );
        authCheck(userId);
        if (user.getUserRole() == Role.BUYER)
            throw new RemoteException("User is a buyer, and cannot sell new auction items!");
        if (reservePrice > startPrice)
            throw new RemoteException("Reserve price cannot be bigger than starting price!");
        var item = ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Invalid request: Auction item with id " + itemId + " does not exist!")
        );
        var newAuctionItem = AuctionItem.of(user, item, reservePrice, startPrice);
        try {
            auctionChannel.send(newAuctionItem.getId(), newAuctionItem);
        } catch (Exception e) {
            throw new RemoteException("Auction could not be created: " + e.getMessage());
        }
        return newAuctionItem.getId();
    }

    @Override
    public boolean close(int auctionId, int ownerId) throws RemoteException {
        if (!users.exists(ownerId))
            throw new RemoteException("User does not exist!");
        authCheck(ownerId);
        var auction = auctionChannel.get().orElseThrow().get(auctionId).orElseThrow(() ->
                new RemoteException("Auction item does not exist!")
        );

        if (auction.getOwner().getId() == (ownerId)) {
            try {
                auctionChannel.sendClose(auctionId);
            } catch (Exception e) {
                throw new RemoteException("Auction could not be closed: " + e.getMessage());
            }
        } else
            throw new RemoteException("You cannot close an auction you do not own!");

        return true;
    }

    @Override
    public Item getSpecAuth(int itemId, int userId) throws RemoteException {
        authCheck(userId);
        System.out.println("Request for user " + userId + ":");
        return ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Bad request: Item with id " + itemId + " does not exist!")
        );
    }

    private void authCheck(int userId) throws RemoteException {
        if (!userSecurity.isAuthenticated(userId, users))
            throw new RemoteException("You cannot do this. Your account has not been authenticated.");
    }


    /*** @deprecated not needed anymore */
    @Override
    @Deprecated
    public SealedObject getSpec(int itemId, SealedObject clientRequest) throws RemoteException {
        System.out.println("Request for encrypted client " + clientRequest);
        Item item = ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Encrypted client failed:\n\tAuction item with id " + itemId + " does not exist!")
        );

        var itemSecurityHelper = new SecurityHelper<Item>();

        if (key == null) {
            return itemSecurityHelper.encrypt(item, true).orElseThrow(
                    () -> new RemoteException("Error: Auction item response could not be encrypted."));
        } else {
            return itemSecurityHelper.encrypt(item, key).orElseThrow(
                    () -> new RemoteException("Error: Auction item response could not be encrypted.")
            );
        }

    }

}
