package server.item;

import java.io.Serializable;

public class Item implements Serializable {
    private final int itemId;
    private final String itemTitle;
    private final String itemDescription;
    private final ItemCondition condition;


    private Item(int itemId, String itemTitle, String itemDescription, ItemCondition condition) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.condition = condition;
    }

    public static Item Of(int itemId, String itemTitle, String itemDescription, ItemCondition condition) {
        return new Item(itemId, itemTitle, itemDescription, condition);
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
