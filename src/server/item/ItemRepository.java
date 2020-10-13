package server.item;

import java.util.Map;
import java.util.Optional;

public class ItemRepository {
    private static final Map<Integer, AuctionItem> repository = Map.of(
            1, AuctionItem.Of(1, "Blue Sock", "A singular, blue sock.", ItemCondition.Refurbished),
            2, AuctionItem.Of(2, "Nintendo Witch", "A witch that's fun for all ages!", ItemCondition.New),
            3, AuctionItem.Of(3, "Red Pom Pom Set", "Go Team! Go team! Go team!", ItemCondition.Used),
            4, AuctionItem.Of(4, "Extra Fine China", "Fine China, but we made it finer.", ItemCondition.Used),
            5, AuctionItem.Of(5, "Cute Covid-19 Plushy", "They can do anything! These are also going viral!", ItemCondition.New)
    );

    public static Optional<AuctionItem> getAuctionItem(int itemId) {
        return Optional.ofNullable(repository.get(itemId));
    }
}
