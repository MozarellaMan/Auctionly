package server.item;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemRepository {
    private static List<AuctionItem> repository = Arrays.asList(
            AuctionItem.Of(1, "Blue Sock", "A singular, blue sock.", ItemCondition.Refurbished),
            AuctionItem.Of(2, "Nintendo Witch", "A witch that's fun for all ages!", ItemCondition.New),
            AuctionItem.Of(3, "Red Pom Pom Set", "Go Team! Go team! Go team!", ItemCondition.Used),
            AuctionItem.Of(4, "Extra Fine China", "Fine China, but we made it finer.", ItemCondition.Used),
            AuctionItem.Of(5, "Cute Covid-19 Plushy", "They can do anything! These are also going viral!", ItemCondition.New)
    );

    public static Optional<AuctionItem> getAuctionItem(int itemId) {
        return repository.stream().filter(item -> item.getItemId() == itemId).findFirst();
    }
}
