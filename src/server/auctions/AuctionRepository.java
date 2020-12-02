package server.auctions;

import server.channels.RepoInterface;

import java.io.Serializable;
import java.util.*;

public class AuctionRepository implements Serializable, RepoInterface {
    private final Map<Integer, AuctionItem> auctions;
    private final Map<Integer, AuctionItem> closedAuctions;

    public AuctionRepository() {
        auctions = new HashMap<>();
        closedAuctions = new HashMap<>();
    }

    public AuctionRepository(Map<Integer, AuctionItem> auctions, Map<Integer, AuctionItem> closedAuctions) {
        this.auctions = auctions;
        this.closedAuctions = closedAuctions;
    }

    public void add(int auctionId, AuctionItem auctionItem) {
        auctions.put(auctionId, auctionItem);
    }

    public Optional<AuctionItem> get(int auctionId) {
        return Optional.ofNullable(auctions.get(auctionId));
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean close(int auctionId) {
        var closedAuction = get(auctionId).orElse(null);
        if (closedAuction == null) return false;
        remove(auctionId);
        closedAuction.close();
        closedAuctions.put(auctionId, closedAuction);
        return true;
    }

    private void remove(int auctionId) {
        auctions.remove(auctionId);
    }

    public List<AuctionItem> list() {
        return new ArrayList<>(auctions.values());
    }

    public List<AuctionItem> listClosed() {
        return new ArrayList<>(closedAuctions.values());
    }

    public void clear() {
        auctions.clear();
        closedAuctions.clear();
    }

    public void addAll(AuctionRepository auctions) {
        this.auctions.putAll(auctions.auctions);
        this.closedAuctions.putAll(auctions.closedAuctions);
    }
}
