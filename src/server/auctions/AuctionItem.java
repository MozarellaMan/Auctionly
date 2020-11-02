package server.auctions;

import server.item.Item;

import java.util.concurrent.ThreadLocalRandom;

public class AuctionItem {
    private final int auctionId;
    private final float reservePrice;
    private final Item item;
    private float latestPrice;

    private AuctionItem(Item item, int reservePrice, int startingPrice) {
        this.auctionId = ThreadLocalRandom.current().nextInt(1, 10000);
        this.reservePrice = reservePrice;
        this.latestPrice = startingPrice;
        this.item = item;
    }

    public static AuctionItem of(Item item, int reservePrice, int startingPrice) {
        return new AuctionItem(item, reservePrice, startingPrice);
    }

    public int getAuctionId() {
        return auctionId;
    }

    public float getLatestPrice() {
        return latestPrice;
    }

    public void bid(float offer) {
        if (offer > this.latestPrice)
            this.latestPrice = offer;
    }

    public Item getItem() {
        return item;
    }
}
