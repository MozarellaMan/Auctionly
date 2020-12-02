package server.auctions;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.util.Util;
import server.channels.RepoChannel;

import java.io.*;

public class AuctionChannel extends ReceiverAdapter implements RepoChannel<AuctionRepository> {
    private final AuctionRepository state = new AuctionRepository();
    private JChannel channel;

    @Override
    public void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.setName("AuctionRepo");
        channel.connect("AuctionCluster");
        channel.getState(null, 10000);
    }

    public void send(int id, AuctionItem item) throws Exception {
        var entry = AuctionEntry.of(id, item);
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
            state.add(obj.id, obj.item);
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    @Override
    public void close() {
        if (channel == null) return;
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

        public AuctionEntry(int id, AuctionItem item) {
            this.id = id;
            this.item = item;
        }

        public static AuctionEntry of(int id, AuctionItem item) {
            return new AuctionEntry(id, item);
        }
    }
}
