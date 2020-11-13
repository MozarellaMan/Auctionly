package server.auctions;

import java.util.*;

public class AuctionRepository {
    private final Map<Integer, AuctionItem> auctions;
    private final Map<Integer, AuctionItem> closedAuctions;

    public AuctionRepository() {
        auctions = new HashMap<>();
        closedAuctions = new HashMap<>();
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

}
