package server.auctions;

import server.channels.RepoCluster;
import server.user.User;

import java.io.Serializable;
import java.util.ArrayList;

public class AuctionRepoCluster extends RepoCluster<AuctionRepository, AuctionChannel> implements Serializable {
    private int startAmount = 3;

    public AuctionRepoCluster() {
        super();
        for (int i = 0; i < startAmount; i++) {
            channels.add(new AuctionChannel());
        }
    }

    public AuctionRepoCluster(int amount) {
        startAmount = amount;
        channels = new ArrayList<>();
        for (int i = 0; i < startAmount; i++) {
            channels.add(new AuctionChannel());
        }
    }

    public void send(int id, AuctionItem item) throws Exception {
        var channel = getChannel().orElseThrow();
        if (channel.isOpen())
            channel.send(id, item);
    }

    public void sendClose(int id) throws Exception {
        var channel = getChannel().orElseThrow();
        if (channel.isOpen())
            channel.sendClose(id);
    }

    @Override
    public void add() throws Exception {
        var newChannel = new AuctionChannel();
        newChannel.start();
        channels.add(newChannel);
    }


    public void sendBid(int id, User user, float offerPrice) throws Exception {
        var channel = getChannel().orElseThrow();
        if (channel.isOpen())
            channel.sendBid(id, user, offerPrice);
    }
}
