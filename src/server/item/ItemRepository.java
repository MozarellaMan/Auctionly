package server.item;

import java.util.Map;
import java.util.Optional;

public class ItemRepository {
    private static final Map<Integer, Item> repository = Map.of(
            1, Item.of(1, "Blue Sock", "A singular, blue sock.", ItemCondition.REFURBISHED),
            2, Item.of(2, "Nintendo Witch", "A witch that's fun for all ages!", ItemCondition.NEW),
            3, Item.of(3, "Red Pom Pom Set", "Go Team! Go team! Go team!", ItemCondition.USED),
            4, Item.of(4, "Extra Fine China", "Fine China, but we made it finer.", ItemCondition.USED),
            5, Item.of(5, "Cute Covid-19 Plushy", "They can do anything! These are also going viral!", ItemCondition.NEW)
    );

    public static Optional<Item> getAuctionItem(int itemId) {
        return Optional.ofNullable(repository.get(itemId));
    }
}
