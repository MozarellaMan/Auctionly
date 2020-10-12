package server;

public class AuctionItem {
    private int itemId;
    private String itemTitle;
    private String itemDescription;


    public static AuctionItem Of(int itemId, String itemTitle, String itemDescription) {
        return new AuctionItem(itemId, itemTitle, itemDescription);
    }

    private AuctionItem(int itemId, String itemTitle, String itemDescription) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }
}
