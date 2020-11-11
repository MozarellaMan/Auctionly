package server;

import server.auctions.AuctionItem;
import server.auctions.AuctionRepository;
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
    private AuctionRepository auctions;
    private UserRepository users;
    private UserSecurity userSecurity;

    protected AuctionService() throws RemoteException {
        super();
    }

    protected AuctionService(String key) throws RemoteException {
        super();
        this.key = key;
        this.auctions = new AuctionRepository();
        this.users = new UserRepository();
        this.userSecurity = new UserSecurity();
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
        if (users.exists(userId))
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
    public List<AuctionItem> getActiveAuctions() throws RemoteException {
        return auctions.list();
    }

    @Override
    public List<AuctionItem> getClosedAuctions() throws RemoteException {
        return auctions.listClosed();
    }

    @Override
    public boolean bid(int auctionId, float offerPrice, int userId) throws RemoteException {
        var auction = auctions.get(auctionId).orElseThrow(() ->
                new RemoteException("Auction item does not exist!")
        );
        var user = users.get(userId).orElseThrow(() ->
                new RemoteException("User does not exist!")
        );
        if (user.equals(auction.getOwner()))
            throw new RemoteException("User is the owner of the auction item. The owner cannot bid!");

        boolean bid = auction.bid(user, offerPrice);

        if (!bid)
            throw new RemoteException("Bid offer is not high enough!");

        return true;
    }

    @Override
    public int sell(float startPrice, float reservePrice, Item item, int userId) throws RemoteException {
        var user = users.get(userId).orElseThrow(() ->
                new RemoteException("User does not exist!")
        );
        var newAuctionItem = AuctionItem.of(user, item, reservePrice, startPrice);
        auctions.add(newAuctionItem.getId(), newAuctionItem);
        return newAuctionItem.getId();
    }

    @Override
    public boolean close(int auctionId, int ownerId) throws RemoteException {
        if (users.exists(ownerId))
            throw new RemoteException("User does not exist!");

        var auction = auctions.get(auctionId).orElseThrow(() ->
                new RemoteException("Auction item does not exist!")
        );

        if (auction.getOwner().getId() == (ownerId))
            auctions.close(auctionId);
        else
            throw new RemoteException("You cannot close an auction you do not own!");

        return true;
    }

    @Override
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

    @Override
    public Item getSpecUnsafe(int itemId, int clientId) throws RemoteException {
        System.out.println("Request for client " + clientId + ":");
        return ItemRepository.getAuctionItem(itemId).orElseThrow(
                () -> new RemoteException("Client " + clientId + " failed:\n\tAuction item with id " + itemId + " does not exist!")
        );
    }
}
