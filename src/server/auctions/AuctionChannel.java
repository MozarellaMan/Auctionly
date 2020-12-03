package server.auctions;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import server.channels.RepoChannel;
import server.user.User;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class AuctionChannel extends ReceiverAdapter implements RepoChannel<AuctionRepository> {
    private final AuctionRepository state = new AuctionRepository();
    private JChannel channel;

    @Override
    public void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.setName("AuctionRepo" + ThreadLocalRandom.current().nextInt(1, 1000));
        channel.connect("AuctionCluster");
        channel.getState(null, 10000);
    }

    public synchronized void send(int id, AuctionItem item) throws Exception {
        var entry = AuctionEntry.of(id, item);
        Message msg = new Message(null, entry);
        channel.send(msg);
    }

    public synchronized void sendClose(int id) throws Exception {
        var entry = new AuctionEntry(id, true);
        Message msg = new Message(null, entry);
        channel.send(msg);
    }

    public synchronized void sendBid(int id, User user, float offerPrice) throws Exception {
        var entry = new AuctionEntry(id, user, offerPrice);
        Message msg = new Message(null, entry);
        channel.send(msg);
    }


    public AuctionRepository getState() {
        return state;
    }

    @Override
    public void setState(InputStream input) throws Exception {
        AuctionRepository auctions;
        auctions = (AuctionRepository) Util.objectFromStream(new DataInputStream(input));
        synchronized (state) {
            state.clear();
            state.addAll(auctions);
        }
        System.out.println(auctions.list().size() + " auctions in auction history.");
        auctions.list().forEach(System.out::println);
    }

    @Override
    public void receive(Message msg) {
        AuctionEntry obj = (AuctionEntry) msg.getObject();
        synchronized (state) {
            if (obj.close) {
                state.close(obj.id);
            } else if (obj.bidder != null && obj.offerPrice > 0) {
                var auctionItem = state.get(obj.id);
                auctionItem.ifPresent(item -> item.bid(obj.bidder, obj.offerPrice));
            } else {
                state.add(obj.id, obj.item);
            }
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    @Override
    public void viewAccepted(View view) {
        System.out.printf("received view %s%n", view);
    }

    @Override
    public void close() {
        if (channel == null) return;
        channel.disconnect();
        channel.close();
    }

    @Override
    public boolean isOpen() {
        if (channel == null) return false;
        return channel.isOpen();
    }

    private static class AuctionEntry implements Serializable {
        public final int id;
        public final AuctionItem item;
        public final boolean close;
        public final float offerPrice;
        public final User bidder;

        public AuctionEntry(int id, AuctionItem item) {
            this.id = id;
            this.item = item;
            this.close = false;
            this.bidder = null;
            this.offerPrice = 0;
        }

        public AuctionEntry(int id, boolean close) {
            this.id = id;
            this.item = null;
            this.close = close;
            this.bidder = null;
            this.offerPrice = 0;
        }

        public AuctionEntry(int id, User user, float offerPrice) {
            this.id = id;
            this.item = null;
            this.close = false;
            this.bidder = user;
            this.offerPrice = offerPrice;
        }

        public static AuctionEntry of(int id, AuctionItem item) {
            return new AuctionEntry(id, item);
        }
    }
}
