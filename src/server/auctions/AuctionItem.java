package server.auctions;

import server.item.Item;
import server.user.User;

import java.util.concurrent.ThreadLocalRandom;

public class AuctionItem {
    private final User owner;
    private final int id;
    private final float reservePrice;
    private final Item item;
    private float latestPrice;

    private AuctionItem(User owner, Item item, int reservePrice, int startingPrice) {
        this.owner = owner;
        this.id = ThreadLocalRandom.current().nextInt(1, 10000);
        this.reservePrice = reservePrice;
        this.latestPrice = startingPrice;
        this.item = item;
    }

    public static AuctionItem of(User owner, Item item, int reservePrice, int startingPrice) {
        return new AuctionItem(owner, item, reservePrice, startingPrice);
    }

    public int getId() {
        return id;
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

    public User getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "Auction Item #" + getId() + "\n\tName: " + item.getItemTitle() + "Desc: " + item.getItemDescription() + "Latest bid: £" + getLatestPrice();
    }
}
