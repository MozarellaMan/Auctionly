package server.item;

import java.io.Serializable;

public class AuctionItem implements Serializable {
    private int itemId;
    private String itemTitle;
    private String itemDescription;
    private ItemCondition condition;


    public static AuctionItem Of(int itemId, String itemTitle, String itemDescription, ItemCondition condition) {
        return new AuctionItem(itemId, itemTitle, itemDescription, condition);
    }

    private AuctionItem(int itemId, String itemTitle, String itemDescription, ItemCondition condition) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.condition = condition;
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

    public ItemCondition getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "AuctionItem {" +
                "itemId=" + itemId +
                ", itemTitle='" + itemTitle + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", condition=" + condition +
                '}';
    }
}
