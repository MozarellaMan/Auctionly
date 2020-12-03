package server.auctions;

import server.item.Item;
import server.user.User;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class AuctionItem implements Serializable {
    private final User owner;
    private User highestBidder;
    private final int id;
    private final float reservePrice;
    private final Item item;
    private float latestPrice;
    private AuctionState state;

    private AuctionItem(User owner, Item item, float reservePrice, float startingPrice) {
        this.owner = owner;
        this.id = ThreadLocalRandom.current().nextInt(1, 10000);
        this.reservePrice = reservePrice;
        this.latestPrice = startingPrice;
        this.item = item;
        this.state = AuctionState.OPEN;
    }

    public static AuctionItem of(User owner, Item item, float reservePrice, float startingPrice) {
        return new AuctionItem(owner, item, reservePrice, startingPrice);
    }

    public int getId() {
        return id;
    }

    public float getLatestPrice() {
        return latestPrice;
    }

    public void bid(User bidder, float offer) {
        if (offer < reservePrice)
            return;
        if (offer > this.latestPrice) {
            this.latestPrice = offer;
            this.highestBidder = bidder;
        }
    }

    public User getOwner() {
        return owner;
    }

    protected void close() {
        this.state = AuctionState.CLOSED;
    }

    @Override
    public String toString() {
        return this.state == AuctionState.OPEN ?
                "Auction Item #" + getId() + "\n\tName: " + item.getItemTitle() + " Latest bid: £" + getLatestPrice() + " Desc: " + item.getItemDescription()
                : "Auction #" + getId() + (highestBidder == null ? " closed by owner" : " won by " + highestBidder.getName()) + " at price £" + getLatestPrice();
    }
}
