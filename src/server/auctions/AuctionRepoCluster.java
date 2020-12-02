package server.auctions;

import server.channels.RepoCluster;

import java.util.ArrayList;

public class AuctionRepoCluster extends RepoCluster<AuctionRepository, AuctionChannel> {
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
        channel.send(id, item);
    }

    @Override
    public synchronized void add() throws Exception {
        var newChannel = new AuctionChannel();
        newChannel.start();
        channels.add(newChannel);
    }


}
