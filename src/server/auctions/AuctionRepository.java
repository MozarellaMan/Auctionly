package server.auctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionRepository {
    public Map<Integer, AuctionItem> auctions;

    public AuctionRepository() {
        auctions = new HashMap<>();
    }

    public void add(int auctionId, AuctionItem auctionItem) {
        auctions.put(auctionId, auctionItem);
    }

    public void remove(int auctionId) {
        auctions.remove(auctionId);
    }

    public List<AuctionItem> listing() {
        return new ArrayList<>(auctions.values());
    }
}
