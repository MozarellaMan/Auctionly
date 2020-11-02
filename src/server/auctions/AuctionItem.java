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

    public boolean bid(float offer) {
        if (offer < reservePrice)
            return false;
        if (offer > this.latestPrice) {
            this.latestPrice = offer;
            return true;
        }
        return false;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "Auction Item #" + getAuctionId() + "\n\tName: " + item.getItemTitle() + "Desc: " + item.getItemDescription() + "Latest bid: £" + getLatestPrice();
    }
}
