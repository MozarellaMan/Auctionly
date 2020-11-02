package server.auctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionRepostiory {
    public Map<Integer, AuctionItem> auctions;

    public AuctionRepostiory() {
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
