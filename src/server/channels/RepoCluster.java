package server.channels;

import org.jgroups.ReceiverAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class RepoCluster<T extends Serializable, A extends ReceiverAdapter & RepoChannel<? extends Serializable>> implements Cluster<T> {
    protected List<A> channels;
    private int startAmount = 3;

    protected RepoCluster() {
        channels = new ArrayList<>();
    }

    protected RepoCluster(int amount) {
        startAmount = amount;
        channels = new ArrayList<>();
    }

    @Override
    public void startAll() {
        if (channels.isEmpty()) return;
        channels.forEach(auctionChannel -> {
            try {
                auctionChannel.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public synchronized void closeAll() {
        if (channels.isEmpty()) return;
        channels.forEach(c -> c.close());
    }

    @Override
    public synchronized void close(int amount) {
        if (channels.isEmpty() || amount > channels.size()) return;
        for (int i = 0; i < amount; i++) {
            if (channels.get(i).isOpen())
                channels.get(i).close();
        }
    }

    @Override
    public void add(int amount) throws Exception {
        for (int i = 0; i < amount; i++) {
            add();
        }
    }

    @Override
    public synchronized void closeOriginalClusters() {
        if (channels.isEmpty() || startAmount > channels.size()) return;
        for (int i = 0; i < startAmount; i++) {
            channels.get(i).close();
        }
    }

    @Override
    public Optional<T> get() {
        var state = (T) channels.get(new Random().nextInt(channels.size())).getState();
        return Optional.ofNullable(state);
    }

    protected Optional<A> getChannel() {
        return Optional.ofNullable(channels.get(new Random().nextInt(channels.size())));
    }

}
